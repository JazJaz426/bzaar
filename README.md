> **GETTING STARTED:** You should likely integrate your work from mock for the `/client` folder and server for the `/server` folder, while keeping some of the new features from the gearup (firebase, fetch, etc.)

## Project Details

- **Project name:** REPL
- **Team Members:**
  - Micah Lessnick (mlessnic)
  - Zhinuo Wang (zwang571)
- **Total Estimated Time:** 40 hours
- **Repo Link:** [GitHub](https://github.com/cs0320-s24/maps-mlessnic-zwang571.git)

## Design Choices

For this sprint, we created a webpage and server application to display a map with redlining data, allowing users and developers to filter the data with keywords or bounding boxes, and add pins to the map that are saved between logins.

Frontend: displays the map in a visual format which allows end user interaction.

- App: The starting component of the front end, directs the flow of information into the login components and mapbox, if login was successful
- AuthRoute: handles displaying the login page and managing cookies to allow users to bypass login if they have logged in before
- LoginLogout: check login credentials for the given user
- MapsGearup: set up the Mapbox component and repl component (repl component depreciated)
- Mapbox: create the map and handle user interaction with the map, specifically adding/removing pins and filtering overlay data
- api: provides helpers to interact with backend endpoints
Depreciated components:
- Retired-components folder: fully depreciated components, from REPL sprint
- ControlledInput: REPL input component, unused
- REPL: high-level REPL organization, invokes the repl input component, history component, unused
- REPLHistory: handles storing and displaying the history of REPL commands, unused
- REPLInput: handles processing REPL commands, unused

Backend: filters overlays with keywords, interacts with firestore db to maintain a user's pins. Also provides api endpoints for load/view/search csv, broadband, and filtering overlays with bounding boxes rather than keyword searches.

- Handlers: responsible for individual function, such as adding/removing pins from a user's db, fetching all GeoJSON data, filtering GeoJSON data, load/view/searching CSVs, retrieve broadband data, etc...
- Storage: interact with firestore to perform the necessary add/remove for a given user's database
- Server: spin up a server at the specified port with all endpoints to interact with the given handlers
- CachedQuery: created cached queries for bounding box query as well as the ACS queries
- Geomap: Geojson parser for parsing GeoJSON into an object with additional filtering methods implemented


## Errors/Bugs


## Testing

The tests are divided into two main categories: server-side tests and client-side tests.

### Server-Side Tests

- **TestCacheSuite:** This suite tests the cache implemented by the CachedACSInfo class for broadband data. It checks that data is stored correctly, and that the cache evicts entries as expected when the cache limit is reached.

- **TestCSVHandlersSuite:** This suite tests the CSV file operation handlers (`loadcsv`, `viewcsv`, `searchcsv`). It ensures that these handlers correctly process both valid and invalid inputs. Tests include successful load, viewing before loading a file, successful view, searching efore loading, searching with invalid parameters, and multiple query search.

- **TestBroadbandHandler:** This suite tests the broadband data fetching functionality. It ensures that the server correctly handles requests for broadband data, including error handling for invalid input parameters and successful data retrieval and formatting.

- **TestGeoJSONHandler:** This suite tests the geojson handler for the bound box queries. It ensures that the server correctly handles the requests for bounding box query, including error handling for invalid input with parameter missing or max value is smaller than min value. Besides this, it also tests if the caching for the bounding box queries works properly.

- **TestSearchAreaHandler:** This suite tests the area searching for keyword queries. It ensures that the server correctly handles the requests for searchArea query, inclusing error handling for missing keyword.


### Client-Side Tests

#### End-to-End (e2e) Tests

The end-to-end (E2E) tests within our REPL application are designed to ensure that the application behaves as expected from the user's perspective. These tests simulate real user interactions with the web interface. We use mocking to simulate backend server responses as well as directly calling the backend server. E2e testing is comprised of the following test suites:

- broadband-test
- element-test
- keystroke-test
- load&search-test
- mocked-load&search-test
- mocked-view-command-test
- mode-test
- view-command-test

#### Unit Tests

- The unit test suite checks the functionality of a specific component or function. This includes testing functions and methods for correct output given a set of inputs, and ensuring that state changes are handled correctly. We use both mocking and integration testing for unit tests. Unit testing is comprised of the following test suites:

- broadbandCommand
- load-test
- mode-test
- search-test
- view-test (real backend integration)

#### Running Tests

#### Client

Go to server/ and run: ./run

Go to client/ and run: npm run test:e2e

#### Server

Go to server/ and run: mvn test

## How to:

### Starting the Application

1. **Start the Server:** Navigate to the server/ directory in your terminal and run the following commands to start the server:

```
mvn package
./run

```

2. **Start the Client:** Open a new terminal window, navigate to the client/ directory, and run:

```
npm install
npm start
```

3. Go to [localhost:8000](http://localhost:8000/) and click Sign in With Google to sign in with your brown edu Google account and now you can access the Map through your web browser.

### Using the Map Interface

The map interface consists of a map and a box to filter the redlining overlay. Here is how to use it:

1. **Moving the map:** left-click and drag on the map to move, right-click to pan, "shift"+"plus" to zoom in, "shift"+"minus" to zoom out.

2. **Adding a pin:** left-click on the map to add a pin at that location.

3. **Removing a pin:** left-click on an existing pin to remove it.

4. **Filtering the overlay:** enter a filter keyword in the filter box at the top and press the button to filter the overlay. Only regions with a description that includes the keyword will remain colored.

### Using the Map APIs
The Map apis includes geojson api (for bounding box) and the search area api.

1. **:geojson api**: provide detailed minLat,maxLat,minLon and maxLon to get filtered geojson data by using /geoquery?minLat=num1&maxLat=num2... Not providing any of them will receive the whole data file. Missing any of the parameter or providing invalid values(like maxLon is smaller than minLon) will result in error.
2.**:searchArea api**: provide keyword to get filtered geojson whose area-description contains the keyword by /areaquery?keyword=somekeyword. Not providing the keyword will result in error. 


## Collaboration

- Micah Lessnick: Worked on frontend mapbox and overlay. Used StackOverflow and LLM for developing the function to remove pins from a user's database.
- Zhinuo Wang: Worked on bounding box api and cached bounding box queries, also search area api. Used online sources to gain ideas of filtering in geojson parser.
