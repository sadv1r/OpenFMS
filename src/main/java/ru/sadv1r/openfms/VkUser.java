package ru.sadv1r.openfms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Пользователь социальной сети Вконтакте
 * Created on 5/4/15.
 *
 * @author sadv1r
 * @version 1.0
 */
@Entity
@Audited
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkUser extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int VK_MIN_ID = 0;
    private static final int VK_MAX_ID = 1000_000_000;
    private static final int VK_STRING_MAX_LENGTH = 50;
    private static final int VK_SITE_MAX_LENGTH = 150;
    private static final int MAX_USERS_TO_PARSE_AT_ONCE = 500;
    @Transient
    private String fieldsToParse = "sex,bdate,city,country,photo_50,photo_100,photo_200_orig,photo_200,photo_400_orig," +
            "photo_max,photo_max_orig,photo_id,online,online_mobile,domain,has_mobile,contacts,connections,site," +
            "education,universities,schools,can_post,can_see_all_posts,can_see_audio,can_write_private_message,status," +
            "last_seen,relation,relatives,counters,screen_name,maiden_name,occupation,activities,interests,music,movies," +
            "tv,books,games,about,quotes,personal,nickname";

    @Id
    @NotNull(message = "id должен быть задан")
    @Min(value = VK_MIN_ID, message = "минимальный id пользователя должен быть: " + VK_MIN_ID)
    @Max(value = VK_MAX_ID, message = "максимальный id пользователя должен быть: " + VK_MAX_ID)
    @JsonProperty("id")
    private int vkId;

    @JsonProperty("first_name")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина имени должна быть: " + VK_STRING_MAX_LENGTH)
    private String firstName;

    @JsonProperty("last_name")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина фамилии должна быть: " + VK_STRING_MAX_LENGTH)
    private String lastName;

    @JsonProperty("sex")
    private boolean sex;

    @JsonProperty("nickname")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина ника должна быть: " + VK_STRING_MAX_LENGTH)
    private String nickname;

    @JsonProperty("maiden_name")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина девичьей фамилии должна быть: " + VK_STRING_MAX_LENGTH)
    private String maidenName;

    @JsonProperty("screen_name")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина screen name должна быть: " + VK_STRING_MAX_LENGTH)
    private String screenName;

    @JsonProperty("bdate")
    @Size(min = 3, max = 10, message = "длина даты рождения должна быть больше 2 и меньше: 11")
    private String birthday;

    @NotAudited
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonProperty("city")
    private City city;

    @Entity
    public static class City implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final int VK_CITY_MIN_ID = 1;
        private static final int VK_CITY_MAX_ID = 1_000_000_000;
        @Id
        @JsonProperty("id")
        @Min(value = VK_CITY_MIN_ID, message = "минимальный id города должен быть: " + VK_CITY_MIN_ID)
        @Max(value = VK_CITY_MAX_ID, message = "максимальный id города должен быть: " + VK_CITY_MAX_ID)
        private int cityId;

        @JsonProperty("title")
        @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина названия города должна быть: " + VK_STRING_MAX_LENGTH)
        private String title;

        public int getCityId() {
            return cityId;
        }

        @JsonSetter("id")
        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getTitle() {
            return title;
        }

        @JsonSetter("title")
        public void setTitle(String title) {
            this.title = title;
        }
    }

    @NotAudited
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonProperty("country")
    private Country country;

    @Entity
    public static class Country implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final int VK_COUNTRY_MIN_ID = 1;
        private static final int VK_COUNTRY_MAX_ID = 300;
        @Id
        @JsonProperty("id")
        @Min(value = VK_COUNTRY_MIN_ID, message = "минимальный id страны должен быть: " + VK_COUNTRY_MIN_ID)
        @Max(value = VK_COUNTRY_MAX_ID, message = "максимальный id страны должен быть: " + VK_COUNTRY_MAX_ID)
        private int countryId;

        @JsonProperty("title")
        @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина названия страны должна быть: " + VK_STRING_MAX_LENGTH)
        private String title;

        public int getCountryId() {
            return countryId;
        }

        @JsonSetter("id")
        public void setCountryId(int countryId) {
            this.countryId = countryId;
        }

        public String getTitle() {
            return title;
        }

        @JsonSetter("title")
        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Transient
    @ManyToOne
    @JsonProperty("schools")
    private School[] schools;

    @Entity
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class School implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonProperty("id")
        private int id;

        @JsonProperty("id")
        private int schoolId;

        @JsonProperty("country")
        private int country;

        @JsonProperty("city")
        private int city;

        @JsonProperty("name")
        private String name;

        @JsonProperty("year_from")
        private int yearFrom;

        @JsonProperty("year_to")
        private int yearTo;

        @JsonProperty("year_graduated")
        private int yearGraduated;

        @JsonProperty("class")
        private String classLetter;

        @JsonProperty("speciality")
        private String speciality;

        @JsonProperty("type")
        private int type;

        @JsonProperty("type_str")
        private String typeStr;

        public int getSchoolId() {
            return schoolId;
        }

        @JsonSetter("id")
        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public int getCountry() {
            return country;
        }

        @JsonSetter("country")
        public void setCountry(int country) {
            this.country = country;
        }

        public int getCity() {
            return city;
        }

        @JsonSetter("city")
        public void setCity(int city) {
            this.city = city;
        }

        public String getName() {
            return name;
        }

        @JsonSetter("name")
        public void setName(String name) {
            this.name = name;
        }

        public int getYearFrom() {
            return yearFrom;
        }

        @JsonSetter("year_from")
        public void setYearFrom(int yearFrom) {
            this.yearFrom = yearFrom;
        }

        public int getYearTo() {
            return yearTo;
        }

        @JsonSetter("year_to")
        public void setYearTo(int yearTo) {
            this.yearTo = yearTo;
        }

        public int getYearGraduated() {
            return yearGraduated;
        }

        @JsonSetter("year_graduated")
        public void setYearGraduated(int yearGraduated) {
            this.yearGraduated = yearGraduated;
        }

        public String getClassLetter() {
            return classLetter;
        }

        @JsonSetter("class")
        public void setClassLetter(String classLetter) {
            this.classLetter = classLetter;
        }

        public String getSpeciality() {
            return speciality;
        }

        @JsonSetter("speciality")
        public void setSpeciality(String speciality) {
            this.speciality = speciality;
        }

        public int getType() {
            return type;
        }

        @JsonSetter("type")
        public void setType(int type) {
            this.type = type;
        }

        public String getTypeStr() {
            return typeStr;
        }

        @JsonSetter("type_str")
        public void setTypeStr(String typeStr) {
            this.typeStr = typeStr;
        }
    }

    @Transient
    @ManyToOne
    @JsonProperty("universities")
    private University[] universities;

    @Entity
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class University implements Serializable {
        private static final long serialVersionUID = 1L;
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @JsonProperty("id")
        private int universityId;

        @JsonProperty("country")
        private int country;

        @JsonProperty("city")
        private int city;

        @JsonProperty("name")
        private String name;

        @JsonProperty("faculty")
        private int faculty;

        @JsonProperty("faculty_name")
        private String facultyName;

        @JsonProperty("chair")
        private int chair;

        @JsonProperty("chair_name")
        private String chairName;

        @JsonProperty("graduation")
        private int graduation;

        public int getUniversityId() {
            return universityId;
        }

        @JsonSetter("id")
        public void setUniversityId(int universityId) {
            this.universityId = universityId;
        }

        public int getCountry() {
            return country;
        }

        @JsonSetter("country")
        public void setCountry(int country) {
            this.country = country;
        }

        public int getCity() {
            return city;
        }

        @JsonSetter("city")
        public void setCity(int city) {
            this.city = city;
        }

        public String getName() {
            return name;
        }

        @JsonSetter("name")
        public void setName(String name) {
            this.name = name;
        }

        public int getFaculty() {
            return faculty;
        }

        @JsonSetter("faculty")
        public void setFaculty(int faculty) {
            this.faculty = faculty;
        }

        public String getFacultyName() {
            return facultyName;
        }

        @JsonSetter("faculty_name")
        public void setFacultyName(String facultyName) {
            this.facultyName = facultyName;
        }

        public int getChair() {
            return chair;
        }

        @JsonSetter("chair")
        public void setChair(int chair) {
            this.chair = chair;
        }

        public String getChairName() {
            return chairName;
        }

        @JsonSetter("chair_name")
        public void setChairName(String chairName) {
            this.chairName = chairName;
        }

        public int getGraduation() {
            return graduation;
        }

        @JsonSetter("graduation")
        public void setGraduation(int graduation) {
            this.graduation = graduation;
        }
    }

    @JsonProperty("photo_id")
    private int photoId;

    @JsonProperty("site")
    @Size(max = VK_SITE_MAX_LENGTH, message = "максимальная длина сайта должна быть: " + VK_SITE_MAX_LENGTH)
    private String site;

    @JsonProperty("twitter")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина твиттера должна быть: " + VK_STRING_MAX_LENGTH)
    private String twitter;

    @JsonProperty("instagram")
    @Size(max = VK_STRING_MAX_LENGTH, message = "максимальная длина инстаграма должна быть: " + VK_STRING_MAX_LENGTH)
    private String instagram;


    public int getVkId() {
        return vkId;
    }

    @JsonSetter("id")
    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public String getFirstName() {
        return firstName;
    }

    @JsonSetter("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonSetter("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean getSex() {
        return sex;
    }

    @JsonSetter("sex")
    public void setSex(int sex) {
        this.sex = sex != 1;
    }

    public String getNickname() {
        return nickname;
    }

    @JsonSetter("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMaidenName() {
        return maidenName;
    }

    @JsonSetter("maiden_name")
    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getScreenName() {
        return screenName;
    }

    @JsonSetter("screen_name")
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getBirthday() {
        return birthday;
    }

    @JsonSetter("bdate")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public City getCity() {
        return city;
    }

    @JsonSetter("city")
    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    @JsonSetter("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    public School[] getSchools() {
        return schools;
    }

    @JsonSetter("schools")
    public void setSchools(School[] schools) {
        this.schools = schools;
    }

    public University[] getUniversities() {
        return universities;
    }

    @JsonSetter("universities")
    public void setUniversities(University[] universities) {
        this.universities = universities;
    }

    public int getPhotoId() {
        return photoId;
    }

    @JsonSetter("photo_id")
    public void setPhotoId(String photoId) {
        this.photoId = Integer.parseInt(photoId.split("_")[1]);
    }

    public String getSite() {
        return site;
    }

    @JsonSetter("site")
    public void setSite(String site) {
        int vkId = this.getVkId();
        if (site.equals("") && vkId == 7364710)
            this.site = "http://ктогей.рф";
        else
            this.site = site;
    }

    public String getTwitter() {
        return twitter;
    }

    @JsonSetter("twitter")
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    @JsonSetter("instagram")
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }


    /**
     * Получает текущий список полей для запроса из Вконтакте
     *
     * @return Список полей через запятую
     */
    public String getFieldsToParse() {
        return fieldsToParse;
    }

    /**
     * Устанавливает список полей для запроса из Вконтакте
     *
     * @param fieldsToParse Список полей через запятую
     */
    public void setFieldsToParse(String fieldsToParse) {
        this.fieldsToParse = fieldsToParse;
    }

    /**
     * Парсит данные пользователя Вконтакте
     *
     * @param vkId Уникальный идентификатор пользователя <b>id</b>
     * @return Объект пользователя
     */
    public VkUser parse(int vkId) throws IOException {
        if (correctVkIdFormat(vkId)) {
            ObjectMapper mapper = new ObjectMapper();
            String documentToParse = "https://api.vk.com/method/users.get?v=5.24&lang=ru&user_ids="
                    + vkId + "&fields=" + fieldsToParse;
            JsonNode usersGetResult = getJsonNodeFromApi(documentToParse).get("response").get(0);
            return mapper.readValue(usersGetResult.toString(), VkUser.class);
        } else {
            throw new IllegalArgumentException("id пользователя имеет недопустимый формат");
        }
    }

    /**
     * Перегрузка метода parse(int vkId)
     *
     * @param screenName Короткое имя пользователя
     * @return Объект пользователя
     * @throws IOException
     * @see #parse(int)
     */
    public VkUser parse(String screenName) throws IOException {
        int vkId = getUserId(screenName);
        return parse(vkId);
    }

    /**
     * Парсит данные пользователей Вконтакте
     *
     * @param ids Уникальные идентификаторы пользователей <b>id</b>. Не более 500
     * @return Объекты пользователей
     * @throws IOException
     */
    public ArrayList<VkUser> parse(int[] ids) throws IOException {
        int idsLength = ids.length;
        if (idsLength <= MAX_USERS_TO_PARSE_AT_ONCE) {
            for (int id : ids) {
                if (!correctVkIdFormat(id)) {
                    throw new IllegalArgumentException("id пользователя имеет недопустимый формат: " + id);
                }
            }
            ArrayList<VkUser> vkUsers = new ArrayList<>(idsLength);
            String stringIds = Arrays.toString(ids).replaceAll("[\\[ \\]]", "");
            ObjectMapper mapper = new ObjectMapper();
            String documentToParse = "https://api.vk.com/method/users.get?v=5.24&lang=ru&user_ids="
                    + stringIds + "&fields=" + fieldsToParse;
            JsonNode usersGetResult = getJsonNodeFromApi(documentToParse).get("response");
            for (int i = 0; i < idsLength; i++) {
                VkUser vkUser = mapper.readValue(usersGetResult.get(i).toString(), VkUser.class);
                vkUsers.add(vkUser);
            }
            return vkUsers;
        } else {
            throw new IllegalArgumentException("Количество идентификаторов не может быть больше " + MAX_USERS_TO_PARSE_AT_ONCE);
        }
    }

    /**
     * Перегрузка метода parse(int[] ids)
     *
     * @param screenNames Короткие имена пользователей
     * @return Объекты пользователей
     * @throws IOException
     * @see #parse(int[])
     */
    public ArrayList<VkUser> parse(String[] screenNames) throws IOException {
        int screenNamesLength = screenNames.length;
        int[] ids = new int[screenNamesLength];
        for (int i = 0; i < screenNamesLength; i++) {
            ids[i] = getUserId(screenNames[i]);
        }
        return parse(ids);
    }

    /**
     * Проверяет id пользователя Вконтакте на соответсвие формату
     *
     * @param vkId Уникальный идентификатор пользователя <b>id</b>
     * @return true, если формат id правильный. false - если неправильный
     */
    private boolean correctVkIdFormat(int vkId) {
        return vkId > VK_MIN_ID && vkId < VK_MAX_ID;
    }

    /**
     * Получает id пользователя Вконтакте по короткому имени
     *
     * @param screenName Короткое имя пользователя
     * @return Уникальный идентификатор пользователя <b>id</b>
     * @throws IOException
     */
    private int getUserId(String screenName) throws IOException {
        JsonNode resolveScreenNameResult = getJsonNodeFromApi("https://api.vk.com/method/utils.resolveScreenName?screen_name="
                + screenName).get("response");
        if (resolveScreenNameResult.hasNonNull("object_id")) {
            return resolveScreenNameResult.get("object_id").asInt();
        } else {
            throw new IllegalArgumentException("Пользователя с таким коротким именем не существует");
        }
    }
}
