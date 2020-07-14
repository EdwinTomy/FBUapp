# Virtual Resume

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
The app is the virtual version of a business card, used to show your achievements in networking events. It also acts as “business card folder” with a list of your friends	achievements, and has a news feed where people upload new achievements.

### App Evaluation
- **Category:** Professional Social Networking 
- **Mobile:** This app would be exclusively developed for mobile 
- **Story:** Used to show your achievements in networking events. It also acts as “business card folder” with a list of your friends achievements, and has a news feed where people upload new achievements.
- **Market:** Students and job seekers
- **Habit:** Whenever attending networking events like career fairs
- **Scope:** First it can be a resume posting site and later it can have the ability for people to publish search posts for possible employees

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can login 
* User can sign up, where:
   * User can chose username, first name, last name and password
   * User can add profile picture
   * User can add description 
   * User can add location	
* User can take, modify and upload a photo
* User see uploaded achievements of contacts
   * User can tap on achievement for detailed view
* User can search for a contact or achievement	
* User can look at contact list and tap each contact for profile view 
* User can view achievements and description of profile
* User can edit profile:	
   * User can add/delete achievement 
   * User can edit profile picture
   * User can edit description 
   * User can edit location
* In creating an achievement:
   * User can add/edit image 
   * User can add/edit description
   * User can add/edit title
* User can see contacts provided location in a map
   * User can see contacts achievements location
* User can logout

**Optional Nice-to-have Stories**

* User can tap on achievement for detailed view in newsfeed
* User can tap on profile in detailed view to navigate to the poster’s profile
* User can add/delete contact
* User can edit profile:		
   * User can edit achievement 
   * User can add/edit/delete skills 
* In creating an achievement:
   * User can add/edit location
   * User can add/edit field
   * User can add/edit organization
   * User can add/edit time of achievement   
* In creating a skill:
   * User can add/edit skill name 
   * User can add/edit skill level
* User can see contacts achievements location in a map

### 2. Screen Archetypes

* Login Screen
   * User can login 
   * User can sign up

* Create User Screen	
   * User can chose username, first name, last name and password
   * User can add profile picture
   * User can add description 
   * User can add location

* Camera Screen	
   * User can take and upload a photo
   * User can crop a photo
   * User can zoom in a photo

* News Feed Screen	
   * User can see uploaded achievements 
   * User can tap on achievement for detailed view

* Achievement Detailed View Screen	
   * User can see uploaded achievements details
   * User can tap on profile picture for profile view

* Contact List Screen	
   * User can add/delete contact
   * User can search for a contact or achievement	
   * User can look at contact list and tap each contact for profile view 

* Profile View Screen	
   * User can view achievements and description of such profile

* Edit Profile Screen	
   * User can add/edit/delete achievement 
   * User can add/edit/delete skills 
   * User can edit profile picture
   * User can edit description 
   * User can edit location

* Create Achievement Screen	
   * User can add/edit image 
   * User can add/edit description
   * User can add/edit location
   * User can add/edit title 
   * User can add/edit field
   * User can add/edit organization
   * User can add/edit time of achievement   

* Create Skills Screen
   * User can add/edit skill name 
   * User can add/edit skill level

* Map Screen	
   * User can see contacts provided location 
   * User can see contacts achievements location

* Settings Screen	
   * User can logout


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Newsfeed tab
* Contact List tab
* Profile View tab
* Profile Edit tab
* Map tab
* Settings tab

All the tabs conform the “Home Screen”

**Flow Navigation** (Screen to Screen)

* Login Screen
   =>Create User Screen
   =>Home Screen

* Create User Screen	
   =>Camera Screen
   =>Home Screen

* Camera Screen	
   =>Previous Screen

* News Feed Screen	
   =>Home Screen
   =>Detailed View Screen

* Detailed View Screen	
   =>Home Screen
   =>News Feed Screen

* Contact List Screen	
   =>Home Screen
   =>Profile View Screen

* Profile View Screen	
   =>Home Screen

* Edit Profile Screen	
   =>Home Screen
   =>Create Achievement Screen
   =>Create Skill Screen
   =>Camera Screen

* Create Achievement Screen	
   =>Home Screen
   =>Edit Profile Screen 

* Create Skills Screen
   =>Home Screen
   =>Edit Profile Screen

* Map Screen	
   =>Home Screen

* Settings Screen	
   =>Home Screen
   =>Login Screen


## Wireframes
<img src="https://github.com/EdwinTomy/FBUapp/raw/master/AppFlow.jpg" width=800><br>

### [BONUS] Digital Wireframes & Mockups
<img src="https://i.imgur.com/lYHn37F.jpg" height=200>

### [BONUS] Interactive Prototype
<img src="https://i.imgur.com/AiKfE5g.gif" width=200>

## Schema 
### Models
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | username        | String| unique username |
   | firstName       | String   | first name of user |
   | lastName       | String   | last name of user |
   | profileImage         | File     | profile image of user |
   | bio       | String   | quick description of user |
   | location       | Location   | location of user |
  

#### Achievement	

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | image         | File     | image of achievement that user posts |
   | description       | String   | description of achievement |
   | title       | String   | position/level in performing achievement |
   | field       | String   | field of achievement |
   | organization       | String   | organization of achievement |
   | time       | DateTime   | time of achievement |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Skill (optional)

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | level       | double   | level of skill |
   | title       | String   | name of skill |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

### Networking
#### List of network requests by screen

* Login Screen
   * (Update/PUT) current user
   * (Create/POST) create user

* News Feed Screen	
   * (Read/GET) achievements of contacts

* Contact List Screen	
   * (Create/POST) add/delete contact
   * (Read/GET)(Delete) search for a contact or achievement	

* Profile View Screen	
   * (Read/GET) view achievements and description of such profile

* Edit Profile Screen	
   * (Create/POST)(Delete)(Update/PUT)  add/edit/delete achievement, skills 
   * (Update/PUT) edit profile picture, description, location

* Map Screen	
   * (Read/GET) all contacts provided location 

* Settings Screen	
   * (Update/PUT) current user

  
#### [OPTIONAL:] Existing API Endpoints
