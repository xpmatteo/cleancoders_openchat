package org.openchat.domain.user;

import org.openchat.infrastructure.persistence.IdGenerator;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final IdGenerator idGenerator;
    private final UserRepository userRepository;

    public UserService(IdGenerator idGenerator, UserRepository userRepository) {
        this.idGenerator = idGenerator;
        this.userRepository = userRepository;
    }

    public User create(RegistrationData registrationData) {
        validateUsername(registrationData.username());
        User user = userFrom(registrationData);
        userRepository.add(user);
        return user;
    }

    private User userFrom(RegistrationData registrationData) {
        return new User(idGenerator.nextId(),
                                    registrationData.username(),
                                    registrationData.password(),
                                    registrationData.about());
    }

    private void validateUsername(String username) {
        if (userRepository.isUsernameInUse(username)) {
            throw new UsernameAlreadyInUseException();
        }
    }

    public Optional<User> userBy(String username, String password) {
        return userRepository.userFor(username, password);
    }

    public Optional<User> userBy(String userId) {
        return userRepository.userForId(userId);
    }

    public void createFollowing(String followerId, String followeeId) {
        Optional<User> follower = userRepository.userForId(followerId);
        Optional<User> followee = userRepository.userForId(followeeId);
        if (!follower.isPresent() || !followee.isPresent()) {
            throw new UserDoesNotExistException();
        }
        userRepository.createFollowing(follower.get(), followee.get());
    }

    public List<User> followeesFor(String userId) {
        return userRepository.followeesFor(userId);
    }

    public List<User> allUsers() {
        return userRepository.all();
    }
}
