package at.ac.tuwien.sepm.groupphase.backend.basetest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String SPOT_BASE_URI = BASE_URI + "/spots";
    String SUBSCRIPTION_URI = SPOT_BASE_URI + "/subscribe";
    String MESSAGE_FILTER_URI = MESSAGE_BASE_URI + "/filter";
    String REACTIONS_BASE_URI = BASE_URI + "/reactions";
    String HASHTAG_BASE_URI = BASE_URI + "/hashtags";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };
    String NAME = "Jannis Favourite Parkour Spot";
    String DESCRIPTION = "The best Spot in Town";
    String SPOT_NAME = "Parkour Spot";
    String SPOT_DESCRIPTION = "At this location one can find the best persons to parkour with";
    String CAT_NAME= "Soccer";
    String CAT_NAME2= "Parkour";
    String MESSAGE_CONTENT = "Hello";
    Double LAT = 10.0;
    Double LONG = 10.0;
    Double LAT2 = -110.0;
    Double LONG2 = -110.0;
    Double LAT4 = 10.0000001;
    Double LONG4 = 10.0000001;
    String EMPTY_NAME = "";
    LocalDateTime DATE = LocalDateTime.of(2021,1,4,18,19,20,100);
    LocalDateTime DATE2 = LocalDateTime.of(2021,5,24,1,15,3,140);
    LocalDateTime DATE_IN_THE_PAST = LocalDateTime.of(2020,5,24,1,15,3,140);
    Double LAT3= 91.57;
    Double LONG3 = -20.3;
    Double RADIUS = 1.0;
    String HASHTAG_NAME= "best";
    String EMAIL = "user@email.com";
    Boolean ENABLED = true;
    String USERNAME = "user";
    String PASSWORD = "password";
    String EMAIL2 = "user2@email.com";
    String USERNAME2 = "user2";
    String PASSWORD2 = "password2";
    String EMAIL3 = "user3@email.com";
    String USERNAME3 = "user3";
    String PASSWORD3 = "password3";
    String EMAIL4 = "user4@email.com";
    String USERNAME4 = "user4";
    String PASSWORD4 = "password4";
    String EMAIL5 = "user5@email.com";
    String USERNAME5 = "user5";
    String PASSWORD5 = "password5";
    String EMAIL6 = "user6@email.com";
    String USERNAME6 = "user6";
    String PASSWORD6 = "password6";
    String DESCRIPTION_WITH_HASHTAGS = "The #best spot in town!";
    String DESCRIPTION_WITH_HASHTAGS2 = "The #secondBest spot in town!";
}