package com.example.ActualApp.service;

import com.example.ActualApp.controller.dto.DescriptionDto;
import com.example.ActualApp.controller.dto.NameAndCountDto;
import com.example.ActualApp.controller.dto.ActivityDto;
import com.example.ActualApp.controller.dto.NewActivityDto;
import com.example.ActualApp.mapper.ActivityMapper;
import com.example.ActualApp.repository.ActivityCategoryRepository;
import com.example.ActualApp.repository.ActivityRepository;
import com.example.ActualApp.repository.entity.Activity;
import com.example.ActualApp.repository.entity.ActivityCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityCategoryRepository categoryRepository;
    private final ActivityMapper activityMapper;


    public ActivityService(ActivityRepository activityRepository, ActivityMapper activityMapper, ActivityCategoryRepository categoryRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.categoryRepository = categoryRepository;
    }

    public List<ActivityDto> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(activityMapper::mapActivityToDto)
                .toList();
    }

    public List<ActivityDto> getAllActivities(Pageable pageable) {
        return activityRepository.findAllBy(pageable).stream()
                .map(activityMapper::mapActivityToDto)
                .toList();
    }

    public ActivityDto getActivityById(UUID id) {
        return activityRepository.findById(id)
                .map(activityMapper::mapActivityToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<ActivityDto> getActivitiesByCategory(UUID id) {
        return categoryRepository.findById(id)
                .map(ActivityCategory::getActivities)
                .map(activities -> activities.stream()
                        .map(activityMapper::mapActivityToDto).toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<ActivityDto> getActivitiesByDate(String date) {
        List<Activity> activities = activityRepository.findAllByDate(LocalDate.parse(date));
        return activities.stream()
                .map(activityMapper::mapActivityToDto)
                .toList();
    }

    public List<NameAndCountDto> getActivitiesByTimeSpent() {
        return activityMapper.mapToNameAndCountDto(activityRepository.getActivitiesByTime());
    }

    public List<NameAndCountDto> getMostOftenNotCompletedActivity() {
        return activityMapper.mapToNameAndCountDto(activityRepository.getMostOftenNotCompletedActivity());
    }

    public NewActivityDto saveNewActivity(NewActivityDto newActivity) {
        ActivityCategory category = categoryRepository.findByName(newActivity.categoryName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        activityRepository.save(activityMapper.mapNewActivityDtoToEntity(newActivity, category));
        return newActivity;
    }

    public ActivityDto toggleCompleted(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        activity.setCompleted(!activity.isCompleted());
        activityRepository.save(activity);
        return activityMapper.mapActivityToDto(activity);
    }

    public ActivityDto updateDescription(UUID id, DescriptionDto description) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        activity.setDescription(description.description());
        activityRepository.save(activity);
        return activityMapper.mapActivityToDto(activity);
    }

    public void deleteActivity(UUID id) {
        activityRepository.deleteById(id);
    }


    public ActivityDto updateActivity(UUID id, ActivityDto activity) {
        Activity activityToUpdate = activityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        activityToUpdate.setDescription(activity.description());
        activityToUpdate.setTimeSpentInMinutes(activity.timeSpentInMinutes());

        return activityMapper.mapActivityToDto(activityRepository.save(activityToUpdate));
    }
}
