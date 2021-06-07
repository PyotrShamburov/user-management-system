# user-management-system
The system is a WEB application that provides an user management interface.<br>
<br>
In project used follows technologies:<br>
- Java 8<br>
- SpringBoot<br>
- SpringDataJPA<br>
- SpringSecurity<br>
- MySQL<br>
- Hibernate-Validator<br>
- Validation-API<br>
- Maven<br>
- Thymeleaf<br>
- Lombok<br>
- JUnit5<br>
- Slf4J<br>
- Bootstrap<br>
<br>
The role model implements in project and they have follows ability: <br>
 <br>
[USER] :<br>
 - Authorization(login)<br>
 - View all available users(list)<br>
 - View details of anybody user(view)<br>
 - Logout(logout)<br>
  <br>
[ADMIN] :<br>
 - Authorization(login)<br>
 - View all available users(list)<br>
 - View details of anybody user(view)<br>
 - Create new user(user/new)<br>
 - Edit existing user(user/edit)<br>
 - Lock/Unlock of user(lock/unlock)<br>
 - Logout(logout)<br>
<br>
Main entity in the project is 'UserAccount'. She has follows fields and their datatypes: <br>
- id - long;<br> 
- username - String;<br>
- password - String;<br>
- firstName - String;<br>
- lastname - String;<br>
- role - (Enum Role(ADMIN, USER));<br>
- status - (Enum UserStatus(ACTIVE, INACTIVE));<br>
- createdAt - String;<br>
<br>
Mapping main entity on the table of database:<br>
-user_id - primary key, not null, autoincrement - int;<br>
- username - unique, not null - varchar(20);<br>
- password - not null - varchar(100);<br>
- first_name - not null - varchar(20);<br>
- last_name - not null - varchar(20);<br>
- role - not null - varchar(20);<br>
- status - not null - varchar(20);<br>
- created_at - not null - varchar(20);<br>
<br>
These views implements in project and they allow do followings:<br>
<br>
/login(authorization in system) :<br>
-entering in system;<br>
-if login/password is wrong or user status is inactive - access is denied;<br>
<br>
/users/{pageNumber} (list of users):<br>
-sort by username;<br>
-filtering by role;<br>
-pagination;<br>
<br>
/user/{id} (view details of user):<br>
-admin can change status active/inactive;<br>
<br>
/user/new (creation new user);<br>
<br>
/user/{id}/edit (editing of existing user);<br>





