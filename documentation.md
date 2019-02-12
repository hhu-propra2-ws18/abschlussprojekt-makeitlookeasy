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

## Model

### User/Person

To represent customers of our platform we decided to split the required information into two classes, `User` and  `Person`.

#### User
To manage the login and permissions we created the `Users` class. Here we store sensitive information like the password or the role.

#### Person
Additional user information, like name or contact is stored in the class `Person`. To connect a Person with a User, a one-to-one relation is provided.

### Article
An article is represented by the `Article` class which simply stores a name and a description.

### Case
The class `Case` represents a contract between loaner and borrower. It links two Users, `owner` and `receiver`, together. Additionally time and pricing information are stored here. For every article borrowed a new case is created.
An article is considered available if no case for the current time is present.
