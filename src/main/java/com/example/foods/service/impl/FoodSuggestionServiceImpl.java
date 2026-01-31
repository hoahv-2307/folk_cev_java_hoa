package com.example.foods.service.impl;

import com.example.foods.dto.request.SuggestionRequestDto;
import com.example.foods.dto.response.SuggestionResponseDto;
import com.example.foods.entity.Food;
import com.example.foods.entity.FoodImage;
import com.example.foods.entity.FoodSuggestion;
import com.example.foods.repository.FoodRepository;
import com.example.foods.repository.FoodSuggestionRepository;
import com.example.foods.service.FileStorageService;
import com.example.foods.service.FoodSuggestionService;
import com.example.foods.service.MailService;
import com.example.foods.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodSuggestionServiceImpl implements FoodSuggestionService {

  private final FileStorageService fileStorageService;
  private final FoodSuggestionRepository suggestionRepository;
  private final MailService mailService;
  private final UserService userService;
  private final FoodRepository foodRepository;

  @Override
  @Transactional
  public SuggestionResponseDto suggestFood(
      SuggestionRequestDto requestDto, Authentication authentication) {
    var user = userService.getUserByUsername(authentication.getName());

    String imageKey = null;
    if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
      imageKey = fileStorageService.uploadFileToTemp(requestDto.getImage());
    }

    FoodSuggestion suggestion =
        FoodSuggestion.builder()
            .userId(user.getId())
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .price(requestDto.getPrice())
            .category(requestDto.getCategory())
            .imageUrl(imageKey)
            .status("PENDING")
            .build();

    suggestion = suggestionRepository.save(suggestion);

    String subject = "New food suggestion: " + suggestion.getName();
    String body =
        "A new food suggestion has been submitted by user: "
            + user.getUsername()
            + "\n\n"
            + "Name: "
            + suggestion.getName()
            + "\nCategory: "
            + suggestion.getCategory()
            + "\nPrice: "
            + suggestion.getPrice()
            + "\n\nView in admin panel: /admin/food-suggestions";
    mailService.sendSimpleMail(subject, body);

    return toDto(suggestion);
  }

  @Override
  @Transactional
  public SuggestionResponseDto processSuggestion(Long id, String status, String adminNote) {
    var suggestion =
        suggestionRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));

    String s = status.toUpperCase();
    if ("APPROVE".equals(s) || "APPROVED".equals(s)) {
      if (suggestion.getImageUrl() != null && suggestion.getImageUrl().startsWith("temp/")) {
        String source = suggestion.getImageUrl();
        String fileName = source.substring(source.indexOf('/') + 1);
        String destKey = "foods/" + fileName;
        fileStorageService.moveFile(source, destKey);
        suggestion.setImageUrl(destKey);
      }

      Food food =
          Food.builder()
              .name(suggestion.getName())
              .description(suggestion.getDescription())
              .category(suggestion.getCategory())
              .price(suggestion.getPrice())
              .quantity(0)
              .status("ACTIVE")
              .build();

      if (suggestion.getImageUrl() != null) {
        FoodImage fi = FoodImage.builder().imageUrl(suggestion.getImageUrl()).build();
        food.addFoodImage(fi);
      }

      foodRepository.save(food);

      suggestion.setStatus("APPROVED");
      suggestion.setAdminNote(adminNote);
      suggestion = suggestionRepository.save(suggestion);
    } else {
      // reject: delete temp image
      if (suggestion.getImageUrl() != null && suggestion.getImageUrl().startsWith("temp/")) {
        fileStorageService.deleteFile(suggestion.getImageUrl());
      }
      suggestion.setStatus("REJECTED");
      suggestion.setAdminNote(adminNote);
      suggestion = suggestionRepository.save(suggestion);
    }

    return toDto(suggestion);
  }

  private SuggestionResponseDto toDto(FoodSuggestion s) {
    return SuggestionResponseDto.builder()
        .id(s.getId())
        .userId(s.getUserId())
        .name(s.getName())
        .description(s.getDescription())
        .price(s.getPrice())
        .category(s.getCategory())
        .imageUrl(s.getImageUrl())
        .status(s.getStatus())
        .adminNote(s.getAdminNote())
        .createdAt(s.getCreatedAt())
        .build();
  }
}
