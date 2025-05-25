package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.UserController;
import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ResourceNotFoundException;
import com.datingapp.datingapp.repository.FieldRepo;
import com.datingapp.datingapp.repository.UserFieldRepo;
import com.datingapp.datingapp.repository.UserInterestRepo;
import com.datingapp.datingapp.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class UserInterestService {

    private final UserFieldRepo userFieldRepo;
    private final FieldRepo fieldRepo;
    private final UserRepo userRepo;
    private final UserInterestRepo userInterestRepo;

    private static final Logger log = LoggerFactory.getLogger(UserInterestService.class);

    public UserInterestService(UserFieldRepo userFieldRepo, FieldRepo fieldRepo, UserRepo userRepo, UserInterestRepo userInterestRepo) {
        this.userFieldRepo = userFieldRepo;
        this.fieldRepo = fieldRepo;
        this.userRepo = userRepo;
        this.userInterestRepo = userInterestRepo;
    }

    @Transactional
    public List<UserFieldDto> getUserProfileFields(int userId) {
        log.info("getUserProfileFields");
        List<ProfileField> defs = fieldRepo.findAllByOrderBySortOrderAsc();
        log.info("defs" + defs);

        Map<Integer, UserProfileField> vals = userFieldRepo.findByIdUserId(userId)
                .stream().collect(toMap(x->x.getId().getFieldId(), identity()));
        log.info("vals" + vals);

        return defs.stream().map(def->{
            UserFieldDto dto = new UserFieldDto();
            dto.setFieldKey(def.getFieldKey());
            dto.setLabel(def.getLabel());
            dto.setDescription(def.getDescription());
            dto.setDataType(def.getDataType());
            UserProfileField upf = vals.get(def.getId());
            dto.setValue(upf!=null ? upf.getDescription() : null);
            return dto;
        }).toList();
    }

    @Transactional
    public void saveUserFields(int userId, List<UserFieldDto> payload) throws ResourceNotFoundException {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        List<ProfileField> definitions =
                fieldRepo.findAllByOrderBySortOrderAsc();
        Map<String, ProfileField> defMap = definitions.stream()
                .collect(Collectors.toMap(ProfileField::getFieldKey, Function.identity()));

        userFieldRepo.deleteByUserId(userId);

        List<UserProfileField> toSave = new ArrayList<>();
        for (UserFieldDto dto : payload) {
            ProfileField def = defMap.get(dto.getFieldKey());
            if (def == null) {
                throw new IllegalArgumentException(
                        "Неизвестное поле профиля: " + dto.getFieldKey());
            }

            UserProfileFieldId id = new UserProfileFieldId(userId, def.getId());

            UserProfileField upf = new UserProfileField();
            upf.setId(id);

            upf.setDescription(dto.getValue());

            toSave.add(upf);
        }

        userFieldRepo.saveAll(toSave);
    }

    @Transactional
    public List<InterestDto> listInterestByIdCategory(int id, List<InterestDto> interests) {
        List<InterestDto> interestDtos = new ArrayList<>();
        for(InterestDto interestDto : interests) {
            if(interestDto.getPkCategory() ==  id)
                interestDtos.add(interestDto);
        }
        return interestDtos;
    }

    @Transactional
    public List<UserInterest> userListInterestByIdCategory(int userId, int categoryId, List<InterestDto> interests) {
        List<UserInterest> userInterests = userInterestRepo.findUserInterestByPkUser(userId);
        if(userInterests.isEmpty()) {
            return null;
        }
        List<UserInterest> finalList = new ArrayList<>();

        for(UserInterest userInterest : userInterests) {
            int interestId = userInterest.getId().getInterestId();
            int categoryIdTemp = -1;
            for(InterestDto interestDto : interests) {
                if(interestDto.getPkInterest() == interestId){
                    categoryIdTemp = interestDto.getPkCategory();
                    break;
                }
            }
            if(categoryIdTemp == categoryId) {
                finalList.add(userInterest);
            }
        }
        return finalList;
    }

    @Transactional
    public List<UserInterest> userListInterest(int userId) {
        return userInterestRepo.findUserInterestByPkUser(userId);
    }

    @Transactional
    public void addUserInterest(int userId, List<UserInterestDto> payload) {
        try{
            log.info("Добавление интересов для юзера с id: " + userId);
            for (UserInterestDto dto : payload) {
                UserInterestId userInterestId = new UserInterestId(userId, dto.getPkInterest());
                UserInterest userInterest = new UserInterest(
                            userInterestId,
                            dto.getWeight() == null ? 1 : dto.getWeight(),
                            dto.getDescription());
                userInterestRepo.save(userInterest);
            }
            log.info("Успешно добавлено "+ payload.size() + " интересов для юзера с id: " + userId);
        }
        catch(Exception e){
            throw new RuntimeException("Ошибка при добавлении интересов: "  + e.getMessage());
        }
    }

    @Transactional
    public void removeUserInterest(int userId, List<UserInterestDto> payload) {
        try{
            log.info("Удаление интересов для юзера с id: " + userId);
            for (UserInterestDto dto : payload) {
                UserInterestId userInterestId = new UserInterestId(userId, dto.getPkInterest());
                UserInterest userInterest = new UserInterest(userInterestId, dto.getWeight(),
                        dto.getDescription());
                userInterestRepo.delete(userInterest);
            }
            log.info("Успешно удалено "+ payload.size() + " интересов для юзера с id: " + userId);
        }
        catch(Exception e){
            throw new RuntimeException("Ошибка при добавлении интересов: "  + e.getMessage());
        }
    }
}
