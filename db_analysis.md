ORM System

We use the code as the database prodType, so there are no problems of having different models in the database and in the code. The code determines how the database is created. The program is the prodType.

But this is where the problems are. We get a lot of safety by relying on the program itself to be the database prodType, but that also means that changes to the program will affect hte data prodType. This is a good thing for prototyping, but could be bad when the app is in production.


Problem 1: Schema is defined in one place.

Problem 2: Schema Information
Code and database have same knowledge of schema. 

Problem 3: The operations
The operations over the database done in the program are kind safe over the schema. That is only valid queries are generated, and in a way which is easy for programmers to understand and make them correct.
