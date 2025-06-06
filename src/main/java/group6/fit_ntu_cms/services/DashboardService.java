package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.repositories.EventRepository;
import group6.fit_ntu_cms.repositories.MediaRePository;
import group6.fit_ntu_cms.repositories.PostRepository;
import group6.fit_ntu_cms.repositories.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;
    private final EventRepository eventRepository;
    private final MediaRePository mediaRePository;

    public DashboardService(PostRepository postRepository,
                            UsersRepository userRepository,
                            EventRepository eventRepository,
                            MediaRePository mediaRePository) {
        this.postRepository = postRepository;
        this.usersRepository = userRepository;
        this.eventRepository = eventRepository;
        this.mediaRePository = mediaRePository;
    }

    public long countArticles() {
        return postRepository.count();
    }

    public long countUsers() {
        return usersRepository.count();
    }

    public long countEvents() {
        return eventRepository.count();
    }

    public long countMedia() {
        return mediaRePository.count();
    }
}
