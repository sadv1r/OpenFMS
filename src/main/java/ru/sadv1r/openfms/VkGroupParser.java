package ru.sadv1r.openfms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created on 7/11/15.
 *
 * @author sadv1r
 * @version 0.1
 */
public class VkGroupParser extends Parser {

    /**
     * Парсит список участников группы Вконтакте
     *
     * @param groupId Уникальный идентификатор пользователя <b>id</b>
     * @return Объект пользователя
     */
    public static void printUsers(int groupId) throws IOException {
        //logger.trace("Запуск метода parse(int)");
        ObjectMapper mapper = new ObjectMapper();
        int offset = 0;
        String vkApiGroupsGetMembersURL = "https://api.vk.com/method/groups.getMembers?v=5.24&lang=ru&group_id=" + groupId + "&offset=";

        String documentToParse = vkApiGroupsGetMembersURL + "0&count=0";
        int groupMembersCount = getJsonNodeFromApi(documentToParse).get("response").get("count").asInt();

        ArrayList<Integer> membersIds = new ArrayList<>();

        while (offset < groupMembersCount) {
            documentToParse = vkApiGroupsGetMembersURL + offset;
            JsonNode groupsGetMembersResult = getJsonNodeFromApi(documentToParse).get("response").get("items");
            for (JsonNode node : groupsGetMembersResult) {
                System.out.println(node.asInt());
            }
            offset += 1000;
        }

        //logger.trace("Получаем основные данные пользователя c id: " + vkId);
        //logger.debug("Получены основные данные пользователя: \"" + vkId + "\"");
    }


    public static ArrayList<Integer> parseUsers(int groupId) throws IOException {
        //logger.trace("Запуск метода parse(int)");

        ArrayList<Integer> membersIds = new ArrayList<>();

        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String targetDomain;
        while ((targetDomain = bufferedReader.readLine()) != null) {
            membersIds.add(Integer.parseInt(targetDomain));
        }

        return membersIds;
    }

}
