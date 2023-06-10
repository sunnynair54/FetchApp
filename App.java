import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//  these provide classes and methods for reading data
import java.net.HttpURLConnection;
import java.net.URI;
//  allows me to open an HTTP connection and interact with the remote server
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// provide classes and interfaces for working with lists, sorting elements, and storing data

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// provide classes and methods for parsing, creating, and manipulating JSON objects and arrays

public class App {

    public static void main(String[] args) {
        // Fetch the data from the URL
        String urlString = "https://fetch-hiring.s3.amazonaws.com/hiring.json";
        // retrieve the JSON data from the URL and return a list of "Item" objects.
        List<Item> itemList = fetchData(urlString);
// check for "null" items 
        if (itemList != null) {
            // filter out "null" data
            itemList = filterItems(itemList);
            // sort the date in ascending order
            itemList = sortItems(itemList);
            // print out ItemIds
            displayItems(itemList);
        }
    }

    private static List<Item> fetchData(String urlString) {
        // I need a place to store the data
        List<Item> itemList = new ArrayList<>();

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
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