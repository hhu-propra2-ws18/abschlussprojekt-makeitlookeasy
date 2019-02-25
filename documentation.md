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

## Model````

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

A simple rating mechanism is implementd using the `CustomerReview` class. It provides the functionality to rate a case with a number of `stars` (higher is better) and add a description to the rating.

### User/Person

To represent customers of our platform we decided to split the required information into two classes, `User` and  `Person`.

#### User
To manage the login and permissions we created the `Users` class. Here we store sensitive information like the password or the role.

#### Person
Additional user information, like name or contact is stored in the class `Person`. To connect a Person with a User, a one-to-one relation is provided.

#### Deviations from the task descriptions
* According to our architectural idea the available offers are **visible even when not logged in.**
