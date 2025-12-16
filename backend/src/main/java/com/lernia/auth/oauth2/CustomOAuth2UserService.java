package com.lernia.auth.oauth2;

import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.AuthProvider;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    // Extract user information from OAuth2User
    String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
    String providerId = oauth2User.getAttribute("sub"); // Google's unique user ID
    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");
    String profilePicture = oauth2User.getAttribute("picture");
     // Modify Google profile picture URL to avoid rate limiting
    if (profilePicture != null && profilePicture.contains("googleusercontent.com")) {
      // Remove size parameter and add s=200 for consistent sizing, and remove authentication
      profilePicture = profilePicture.replaceAll("=s\\d+-c", "=s200-c");
      if (!profilePicture.contains("=s")) {
        profilePicture = profilePicture + "=s200-c";
      }
    }

    // Find or create user
    UserEntity user = findOrCreateUser(provider, providerId, email, name, profilePicture);

    // Return custom OAuth2User wrapper
    return new CustomOAuth2User(oauth2User, user);
  }

  private UserEntity findOrCreateUser(String provider, String providerId, String email, String name,
      String profilePicture) {
    AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());

    // First, try to find by provider and providerId
    Optional<UserEntity> userOpt = userRepository.findByProviderAndProviderId(authProvider, providerId);

    if (userOpt.isPresent()) {
      // User exists, update information if needed
      UserEntity user = userOpt.get();
      boolean updated = false;

      if (name != null && !name.equals(user.getName())) {
        user.setName(name);
        updated = true;
      }

      if (email != null && !email.equals(user.getEmail())) {
        user.setEmail(email);
        updated = true;
      }

      if (profilePicture != null && !profilePicture.equals(user.getProfilePicture())) {
        user.setProfilePicture(profilePicture);
        updated = true;
      }

      if (updated) {
        userRepository.save(user);
      }

      return user;
    }

    // Check if a user with this email already exists (for account linking)
    Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(email);
    if (existingUserByEmail.isPresent()) {
      UserEntity existingUser = existingUserByEmail.get();
      // Link Google account to existing user
      existingUser.setProvider(authProvider);
      existingUser.setProviderId(providerId);
      if (profilePicture != null) {
        existingUser.setProfilePicture(profilePicture);
      }
      return userRepository.save(existingUser);
    }

    // Create new user
    UserEntity newUser = new UserEntity();
    newUser.setProvider(authProvider);
    newUser.setProviderId(providerId);
    newUser.setEmail(email);
    newUser.setName(name);
    newUser.setUsername(email); // Use email as username for OAuth users
    newUser.setProfilePicture(profilePicture);
    newUser.setGender(Gender.OTHER);
    newUser.setUserRole(UserRole.REGULAR);
    newUser.setCreationDate(LocalDate.now());
    newUser.setPassword(null); // No password for OAuth users

    return userRepository.save(newUser);
  }
}
