import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {

    public static void main(String[] args) {
        String url = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
        List<Item> itemList = fetchData(url);

        if (itemList != null) {
            itemList = filterItems(itemList);
            itemList = sortItems(itemList);
            displayItems(itemList);
        }
    }

    private static List<Item> fetchData(String urlString) {
        List<Item> itemList = new ArrayList<>();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    int listId = jsonObject.getInt("listId");
                    String name = jsonObject.getString("name");

                    Item item = new Item(id, listId, name);
                    itemList.add(item);
                }
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    private static List<Item> filterItems(List<Item> itemList) {
        List<Item> filteredList = new ArrayList<>();

        for (Item item : itemList) {
            if (item.getName() != null && !item.getName().isEmpty()) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    private static List<Item> sortItems(List<Item> itemList) {
        Collections.sort(itemList, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                if (item1.getListId() == item2.getListId()) {
                    return item1.getName().compareTo(item2.getName());
                } else {
                    return Integer.compare(item1.getListId(), item2.getListId());
                }
            }
        });

        return itemList;
    }

    private static void displayItems(List<Item> itemList) {
        for (Item item : itemList) {
            System.out.println("List ID: " + item.getListId() + ", Name: " + item.getName());
        }
    }

    private static class Item {
        private int id;
        private int listId;
        private String name;

        public Item(int id, int listId, String name) {
            this.id = id;
            this.listId = listId;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public int getListId() {
            return listId;
        }

        public String getName() {
            return name;
        }
    }
}