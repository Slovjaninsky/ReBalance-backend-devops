| Method        | URL           | Action        |
| ------------- | ------------- | ------------- |
| POST | /users  | create a new User without Groups, body return login and password  |
| POST | /users/:id/groups  | create/add Group for a User  with the given Id|
| POST | /users/email/:email/groups  | create/add Group for a User  with the given Email|
| GET  | /groups/:id/users  | get all Users of a Group  |
| GET  | /users/:id/groups  | get all Groups of a User  |
| GET  | /users/email/:email/groups  | get all Groups of a User by Email|
| GET  | /users  | get all Users  |
| GET  | /users/:id  | get User by ID  |
| GET  | /users/email/:email  | get User by Email  |
| PUT  | /users/email/:email  | update User by Email  |
| PUT  | /users/:id  | update User by Id  |
| GET  | /groups  | get all Groups  |
| GET  | /groups/:id  | get Group by ID  |
| PUT  | /groups/:id  | update Group by ID  |
| DELETE  | /groups/:id  | delete Group by ID  |
| DELETE  | /users/:id  | delete User by ID  |
| DELETE  | /users/email/:email  | delete User by Email  |
| DELETE  | /groups/:id/users/:id  | delete User with the given ID from the given group  |
| POST  | /expenses/user/:id/group/:id  | create and expense with Group and User by IDs |
| GET  | /expenses  | get all Expenses  |
| GET  | /expenses/:globalId  | get all Expenses by global ID  |
| GET  | /groups/:id/expenses  | get all Expenses of the Group by ID  |
| GET  | /groups/:id/users/:id/expenses  | get all Expenses of the User in the Group by ID |
| GET  | /expenses/group/:groupId/from/:dateFirst/:period  | get all Expenses from Group from date inclusive to some period (day, week, month, year) |
| GET  | /expenses/group/:groupId/between/:dateFirst/:dateSecond  | get all Expenses from Group between two dates inclusive |
| PUT  | /expenses/:globalId  | update all Expenses name by global ID  |
| DELETE  | /expenses/:globalId  | delete all Expenses by global ID  |
| GET  | /user/login/:email  | login with the given Email and password  |
| GET  | /expenses/group/:groupId/dates  | get all dates when expenses occurred in the group  |
| POST  | /expenses/:globalId/image  | save Base64 encoded image for expense |
| GET  | /expenses/:globalId/image  | get Base64 encoded image for expense |
| PATCH  | /expenses/:globalId/image  | change Base64 encoded image for expense  |
| DELETE  | /expenses/:globalId/image  | delete image for expense |
| GET  | /connect/test  | connectivity test  |
