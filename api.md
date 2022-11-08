| Method        | URL           | Action        |
| ------------- | ------------- | ------------- |
| POST | /users  | create a new User without Groups  |
| POST | /users/:id/groups  | create/add Group for a User  |
| GET  | /groups/:id/users  | get all Users of a Group  |
| GET  | /users/:id/groups  | get all Groups of a User  |
| GET  | /users  | get all Users  |
| GET  | /users/:id  | get User by ID with their Groups  |
| GET  | /groups  | get all Groups  |
| GET  | /groups/:id  | get Group by ID  |
| PUT  | /groups/:id  | update Group by ID  |
| DELETE  | /groups/:id  | delete Group by ID  |
| DELETE  | /users/:id  | delete User by ID  |
| DELETE  | /groups/:id/users/:id  | delete User with the given ID from the given group  |
| POST  | /expenses/user/:id/group/:id  | create and expense with Group and User by IDs |
| GET  | /expenses  | get all Expenses  |
| GET  | /expenses/:id  | get Expense by ID  |
| GET  | /groups/:id/expenses  | get all Expenses of the Group by ID  |
| GET  | /groups/:id/users/:id/expenses  | get all Expenses of the User in the Group by ID |
| PUT  | /expenses/:id  | update Expense by ID  |
| DELETE  | /expenses/:id  | delete Expense by ID  |