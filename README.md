
## Project Details

- **Project name:** Br-Zaar
- **Team Members:**
  - Jiaming Lin (jlin223)
  - Xinyang Cai (cxinyang)
  - Zhinuo Wang (zwang571)
- **Total Estimated Time:** 100 hours
- **Repo Link:** [GitHub](https://github.com/cs0320-s24/term-project-candice-novia-jaz)

## Purpose:
Bearzaar aims to provide a user-friendly, efficient, and secure platform for trading second-hand goods within the Brown University community. It addresses the inefficiencies of current platforms by offering a streamlined user experience with enhanced features like real-time notifications, a clean interface, and user verification through Brown email addresses.

## Intended Audience:
The primary users are Brown University students interested in a sustainable and cost-effective way to buy and sell goods. The platform is designed for personal use with varying frequencies of engagement, from daily to occasional use.

## Scope and User Stories:
Bearzaar will focus on essential functionalities to facilitate the buying and selling process while ensuring user security and a pleasant user experience. 

Key features include:
- Posting Items: Users can post items for sale
- Browsing and Viewing Items: A catalog of available goods will be displayed, with detailed views available for each item.
- Searching and Watching Items: Users can search for items and add them to a watch list to receive notifications.
- Claiming Items: Items can be claimed to update their status in real-time, preventing others from claiming the same item.
- Discovering Items: Personalized item recommendations can be viewed in the discover page.

## Design Choices:
- Frontend: Utilizes React for a responsive and intuitive user interface.
- Backend: Java is used for server-side logic, handling API endpoints, and interacting with the Firestore database.
- Database: Firestore is chosen for its scalability and real-time data syncing capabilities.
- Authentication: Integration with Brown University's authentication system or Firebase Authentication to manage user logins securely.

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

3. Go to [localhost:8000](http://localhost:8000/) and click Sign in With Google to sign in with your brown edu Google account and now you can access the BZaar through your web browser.


## Collaboration

- Jiaming Lin (jlin223): Focused on optimizing the algorithm complexity for the recommendation/discover page using item-based collaborative filtering. Enhanced the frontend aesthetics and functionality, including adding like buttons and loading prompts on the search and discover pages, and fixing numerous bugs to improve user experience.
- Xinyang Cai (cxinyang): Developed backend endpoints such as recordUserActivity, modifyWatchList, and search, along with their corresponding frontend implementations. Provided debugging support to team members and contributed to backend unit and integration testing.
- Zhinuo Wang (zwang571): Implemented a "post" button for featured works, including functionalities for deleting posts and providing frontend user feedback. Enhanced user data connections to the user's sell list and added backend support functions like getSellingList to improve item retrieval efficiency. Also contributed to frontend testing.
