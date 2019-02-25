# ausleiherino24

## General

The application ausleiherino24 acts as a platform to lend and borrow all sorts of articles.

The key aspects are:

* User management
* Overview over all available articles
* Detailed view of every article
* Possibility for logged in users to offer an article
* Possibility for logged in users to borrow an       article
* Payment of borrowed articles using ProPay
* Conflict resolution

### Functionality

## 

### Design pattern

The designpattern we are aiming for is the classical Model-View-Controller (MVC) pattern. To outsource shared code from the controllers we introduced a additional service layer.
Although this is conform with the MVC pattern it is also refered to as
[Model-View-Controller-Service](https://glossar.hs-augsburg.de/Model-View-Controller-Service-Paradigma) pattern (MVCS).

### Project structure

The project structure follows the design pattern with model, view and controller (here named web) packages.
Logic to interact with propay is located in a a separet propayhandler package.

## Model

#### Article
An article is represented by the `Article` class. Next to descriptve features like name, description and image, it has realtions to the owner of the article (instance of `User`) and to related cases. Custom logic is implementd to keep the article in sync with its related cases and retreive information about the current state of the article.

#### Case
The class `Case` represents a contract between loaner and borrower. It links two Users, `receiver` and the owner of the article, together.
Additionally time, pricing and status informations are stored here. For every article borrowed a new case is created.
An article is considered available if no case for the current time is present.

### Category
The `Category` ennummeration provides simple tags for articles.
These tags enable a simple structuring of articles.

### ChatMessage

To implement a simple user chat, the `ChatMessage` class was needed. It is a simple data class that, in essence, stores the text, receiver and sender of a chat message.

### Conflict

To manage conflicts in the rental process the `Conlict` class was introduced. It wraps the conflicted case with a description and a user who is responible to resolve the conlict.

### CustomerReview

A simple rating mechanism is implementd using the `CustomerReview` class. 
It links a numeric rating (`stars` (higher is better)) and a corresponding description to a case.

### CustomUserDetails
The `CustomUserDetails` class is simply needed to use our own `User` class with spring security.

### PPTransaction
The `PPTransaction` class represents a proPay transaction.

### ResolveConflict
TODO: wright description

### User/Person

To represent customers of our platform we decided to split the required information into two classes, `User` and  `Person`.

#### User
To manage the login and permissions we created the `Users` class. Here we store sensitive information like the password or the role.

#### Person
Additional user information, like name or contact is stored in the class `Person`. To connect a Person with a User, a one-to-one relation is provided.


## Controler

### ImageController

The `ImageController` essenntialy maps the services provided by the `ImageService` to appropriate endpoints.



## Services

### ImageService

The `ImageService` is used to upload images to a file system and retrieve them.
Images are stored in a configurable directory (outside of the project). Methods to store a image in the form of a `File` or a `MultipartFile` object are provided. Each stored image is named by a generated UID followed by an appropriate file extension.

Additionaly on can provide a number to the storing methods (`binningId`). This so called binning id is used to store files in a specific subdirectory. This leads to a B-tree like structure which can speed up the search for a speciffic image.


## Deviations from the task descriptions
* According to our architectural idea the available offers are **visible even when not logged in.**
