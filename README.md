# prog4_asg3

This application is an extension of assignment 1, simplifying its features and implementing a mobile GUI through Android Studio

It primarily functions as a simple inventory management interface; reading in the whole inventory list from items.txt, it display a list of items on the homepage. Each is clickable and leads the user to the item's own page. This list is searchable, leading to the creation of a new list based on the users query. The user is also able to create new items through a form-like interface, which adds the item to "the database" and is displayed in the master item list.

## To Run:
- Running the application on an emulator (or by hooking up your own phone to your computer) will display the homepage.
- The searchbar will generate a new inventory list based on the user query.
- The inventory list is scrollable interactive: tapping on an item will open that item's page
- Tapping the Add Item will open a new activity and display a series of EditText components for the user to enter in the new item data. Then, the user can tap a button to return the values to MainActivity and writes the contents of the new Item to the arrayList

## REST API:
http://34.68.196.188:8080/
Method | Endpoint | Params | Description
--- | --- | --- | ---
GET | /api/items/ | | Find list of all items
GET | /api/items/name=Googly%20Eyes | name | Find specific item by name
GET | /api/items/id=1000 | id | Find specific item by ID
POST | /api/items/ | id, name, quantity, price, supplier_id | Adds a new Item
