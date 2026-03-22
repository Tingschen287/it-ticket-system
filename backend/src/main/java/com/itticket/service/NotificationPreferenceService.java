package com.itticket.service;

import com.itticket.entity.NotificationPreference;
import com.itticket.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public Optional<NotificationPreference> findByUserId(Long userId) {
        return preferenceRepository.findByUserId(userId);
    }

    @Transactional
    public NotificationPreference getOrCreatePreference(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference pref = NotificationPreference.builder()
                            .userId(userId)
                            .emailEnabled(true)
                            .inAppEnabled(true)
                            .ticketAssigned(true)
                            .ticketStatusChanged(true)
                            .ticketCommented(true)
                            .slaWarning(true)
                            .build();
                    return preferenceRepository.save(pref);
                });
    }

    @Transactional
    public NotificationPreference updatePreference(Long userId, NotificationPreference preference) {
        NotificationPreference existing = getOrCreatePreference(userId);
        existing.setEmailEnabled(preference.getEmailEnabled());
        existing.setInAppEnabled(preference.getInAppEnabled());
        existing.setTicketAssigned(preference.getTicketAssigned());
        existing.setTicketStatusChanged(preference.getTicketStatusChanged());
        existing.setTicketCommented(preference.getTicketCommented());
        existing.setSlaWarning(preference.getSlaWarning());
        return preferenceRepository.save(existing);
    }
}
