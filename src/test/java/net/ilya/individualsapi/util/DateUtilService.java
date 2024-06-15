package net.ilya.individualsapi.util;

import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.request.KeyCloakRegistryCredentials;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.*;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.entity.StatusEntity;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DateUtilService {

    public static KeyCloakRegistryCredentials keyCloakRegistryCredentials(){
        return KeyCloakRegistryCredentials.builder()
                .username("test")
                .password("test")
                .build();
    }
    public static IndividualDto getIndividualWithDate(){
        return IndividualDto.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("test@foo.com")
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .build();
    }
    public static IndividualRegistrationRequest getIndividualRegistrationDto(){
        return IndividualRegistrationRequest.builder()
                .build();
    }
    public static IndividualDto getIndividualWithOutDate(){
        CountryDto build = CountryDto.builder()
                .alpha2("US")
                .alpha3("USA")
                .name("Undated states of America")
                .build();
        AddressDto newYork = AddressDto.builder()
                .country(build)
                .city("New York")
                .address("st. Tisovaya 8")
                .zipCode("360078")
                .build();
        UserDto build1 = UserDto.builder()
                .address(newYork)
                .firstName("Guts")
                .lastName("Berserk")
                .secretKey("Best result")
                .build();
        return IndividualDto.builder()
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("GutsBerserk@javan.sun")
                .status(StatusEntity.ACTIVE)
                .userData(build1)
                .build();
    }
    public static IndividualDto getIndividualWithOutDateForUpdate(){
        CountryDto build = CountryDto.builder()
                .alpha2("JP")
                .alpha3("JPS")
                .name("Japan sun country")
                .build();
        AddressDto newYork = AddressDto.builder()
                .country(build)
                .city("Tokyo")
                .address("st. Yakudza 665")
                .zipCode("360078")
                .build();
        UserDto build1 = UserDto.builder()
                .address(newYork)
                .firstName("Guts")
                .lastName("Berserk")
                .secretKey("Best result")
                .build();
        return IndividualDto.builder()
                .passportNumber("230-004 660 066")
                .phoneNumber("+495 000 66 77")
                .email("gutsaftedupdate@japan.sun")
                .status(StatusEntity.UPDATED)
                .userData(build1)
                .build();
    }
    public static IndividualDto getIndividualWithDateAfterUpdate(){
        CountryDto build = CountryDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .alpha2("JP")
                .alpha3("JPS")
                .name("Japan sun country")
                .status(StatusEntity.UPDATED)
                .build();
        AddressDto newYork = AddressDto.builder()
                .country(build)
                .id(UUID.randomUUID())
                .countryId(build.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .archived(LocalDateTime.now())
                .city("Tokyo")
                .address("st. Yakudza 665")
                .zipCode("360078")
                .build();
        UserDto build1 = UserDto.builder()
                .id(UUID.randomUUID())
                .address(newYork)
                .addressId(newYork.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(true)
                .firstName("Guts")
                .lastName("Berserk")
                .secretKey("Best result")
                .build();

        return IndividualDto.builder()
                .id(UUID.randomUUID())
                .userId(build1.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.UPDATED)
                .passportNumber("230-004 660 066")
                .phoneNumber("+495 000 66 77")
                .email("gutsaftedupdate@japan.sun")
                .status(StatusEntity.UPDATED)
                .userData(build1)
                .build();
    }
    public static IndividualDto getIndividualWithDateAfterSave(){
        CountryDto build = CountryDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .alpha2("US")
                .alpha3("USA")
                .name("Undated states of America")
                .status(StatusEntity.ACTIVE)
                .build();
        AddressDto newYork = AddressDto.builder()
                .country(build)
                .id(UUID.randomUUID())
                .countryId(build.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .archived(LocalDateTime.now())
                .city("New York")
                .address("st. Tisovaya 8")
                .zipCode("360078")
                .build();
        UserDto build1 = UserDto.builder()
                .id(UUID.randomUUID())
                .address(newYork)
                .addressId(newYork.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .filled(true)
                .firstName("Guts")
                .lastName("Berserk")
                .secretKey("Best result")
                .build();

        return IndividualDto.builder()
                .id(UUID.randomUUID())
                .userId(build1.getId())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(StatusEntity.ACTIVE)
                .passportNumber("1345234562456")
                .phoneNumber("78235672345")
                .email("GutsBerserk@javan.sun")
                .status(StatusEntity.ACTIVE)
                .userData(build1)
                .build();
    }
    public static UserDto getUserDtoWithoutDate() {
        return UserDto.builder()
                .secretKey("Key")
                .firstName("Petr")
                .lastName("Frolov")
                .build();
    }
    public static UserRepresentation getUserRepresentationJohnWik(){
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("j.wik");
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setFirstName("john");
        newUser.setLastName("wik");
        newUser.setEmail("john@foo.com");

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue("password");
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        newUser.setCredentials(List.of(credentialRepresentation));
        newUser.singleAttribute("personId", UUID.randomUUID().toString());
        return newUser;
    }
    public static KeyCloakRegistryCredentials getGutsBerserkRegistryCredentials(){
        return KeyCloakRegistryCredentials.builder()
                .username("g.berserk")
                .password("password")
                .build();
    }
}
