# ausleiherino24

## General
The application "ausleiherino24" acts as a platform to lend, borrow and sell all sorts of articles.

The key aspects are:

* User management
* Overview over all available articles
* Detailed view of every article
* Possibility for logged in users to offer an article
* Possibility for logged in users to borrow an article
* Payment of borrowed articles using ProPay
* Conflict resolution

## Management

* To provide a convenient overview of our project we used **javadocs** throughout the project.
* Since the project requires a certain amount of configuration we started our own **wiki** in github. Here we describe the different configurations and the initial setup.

## Functionality

### Main page

The main age is visible for everybody, even if not logged in. It displays all available Products.
If a product is rented it is not displayed. The Articles are categorized and the category of the
displayed articles can be selected. Additionally the articles can be searched by name.

### Detailed article view

The detailed article view are only accessible to logged in users.
Next to the article details it displays the article reviews and provides a button to rent or buy the article.

### Booking modal

As soon as a article shall be booked (by pressing the booking button), a modal is displayed.
As soon the booking period was selected the booking can be confirmed.

### Bank account

In the personal bank account the current balance can be viewed,
new money can be requested and past transactions be viewed.

### Profile

On the profile page, personal information can be viewed and edited.

### Overview

The overview page provides a set of functionality:
* **My articles:** Overview over own articles with the options to update or delete them.
* **Borrowed:** Overview over borrowed articles.
* **Returned:** Overview over returned articles with the option to accept the return or open a conflict.
* **Request:** Overview over requested articles with the option to accept or decline the request.
* **Sold:** Overview over sold articles.

### Add Item

On the Add item page a new item can be created. Special notice is to be taken to the `For Sale` check box.
This checkbox determines if a article is for sale or if it only can be borrowed.

## Project overview

### Design pattern
The design pattern we are aiming for is the classical Model-View-Controller (MVC) pattern. To outsource shared code from the controllers we introduced a additional service layer.
Although this is conform with the MVC pattern it is also referred to as
[Model-View-Controller-Service](https://glossar.hs-augsburg.de/Model-View-Controller-Service-Paradigma)
pattern (MVCS).

### Project structure
The project structure follows the design pattern with model, view and controller (here named web)
packages. Logic to interact with ProPay is located in a separate ProPay-handler package.

## Model

### Article
An article is represented by the `Article` class. Next to descriptive features like name,
description and image, it has relations to the owner of the article (instance of `User`) and to
related cases. Custom logic is implemented to keep the article in sync with its related cases and
retrieve information about the current state of the article.

### Case
The class `Case` represents a contract between loaner and borrower. It links two Users, `receiver`
and the owner of the article, together. Additionally time, pricing and status information are stored
here. For every article borrowed a new case is created. An article is considered available if no
case for the current time is present.

### Category
The `Category` enumeration provides simple tags for articles.
These tags enable a simple structuring of articles.

### ChatMessage
To implement a simple user chat, the `ChatMessage` class was needed. It is a simple data class that,
in essence, stores the text, receiver and sender of a chat message.

### Conflict
To manage conflicts in the rental process the `Conflict` class was introduced. It wraps the
conflicted case with a description and a user who is responsible to resolve the conflict.

### CustomerReview
A simple rating mechanism is implemented using the `CustomerReview` class. 
It links a numeric rating (`stars` (higher is better)) and a corresponding description to a case.

### CustomUserDetails
The `CustomUserDetails` class is simply needed to use our own `User` class with spring security.

### PPTransaction
The `PpTransaction` class represents a proPay transaction.

### ResolveConflict
TODO: wright description

### User/Person
To represent customers of our platform we decided to split the required information into two
classes, `User` and  `Person`.

#### User
To manage the login and permissions we created the `Users` class. Here we store sensitive
information like the password or the role.

#### Person
Additional user information, like name or contact is stored in the class `Person`. To connect a
Person with a User, a one-to-one relation is provided.


## Controller

### ArticleController

TODO: provide description

### CaseController

TODO: provide description

### ChatController

The in essence the`ChatController` maps the received chat messages to the endpoints of the Spring `MessageBroker`.

### ConflictController

TODO: provide description

### ImageController
The `ImageController` essentially maps the services provided by the `ImageService` to appropriate
endpoints.

### MainController

TODO: provide description

### UserController

TODO: provide description

## Services

### ArticleService

TODO: provide description

### CaseService

TODO: provide description

### ConflictService

TODO: provide description

### CustomerReviewService

TODO: provide description

### ImageService
The `ImageService` is used to upload images to a file system and retrieve them.
Images are stored in a configurable directory (outside of the project). Methods to store a image in
the form of a `File` or a `MultipartFile` object are provided. Each stored image is named by a
generated UID followed by an appropriate file extension.

Additionally on can provide a number to the storing methods (`binningId`). This so called binning id
is used to store files in a specific subdirectory. This leads to a B-tree like structure which can
speed up the search for a specific image.

### PersonService

TODO: provide description

### SearchUserService

TODO: provide description

### UserService

TODO: provide description

## Security

TODO: provide short description about the spring security configuration

## PropayHandler

### AccountHandler

TODO: provide description

### PpAccount

TODO: provide description

### Reservation

TODO: provide description

### Reservation Handler

TODO: provide description

## Deviations from the task descriptions

* According to our architectural idea the available offers are **visible even when not logged in.**


## Committed production file.
We have noticed, that a production file had been pushed to the 'master'-branch, even though the
folder containing it had been *explicitly* ignored in the project's _.gitignore_.
In order to not majorly disrupt the team's workflow and to ensure a qualitative end result,
we have consulted with [Mr. David Schneider](https://github.com/bivab) to not revert the git-history
and keep it as is, but we have deleted the unwanted file from the repository in commit
[c90bb08](https://github.com/hhu-propra2/abschlussprojekt-makeitlookeasy/commit/c90bb08f5ef96a8248156b6f9da2e6f95dc6d4a9).
