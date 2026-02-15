# Test Cases

## User

### TC-USERS-01 Create user (happy path)
**Given**
- User exists: role = ADMIN

**When**
- Create user request: username = test_user, password = test_password, roles = [USER]

**Then**
- User is created

### TC-USERS-02 Get all users (happy path)
**Given**
- 20 users exist: id = {userId}, role = {ANY_ROLES}

**When**
- Create user request: page = 0, size = 10

**Then**
- Get users response contains 10 users
- Total elements = 20
- Total pages = 2

### TC-USERS-03 Get user by id (happy path)
**Given**
- User exists: id = {userId}, role = USER

**When**
- Create user request with path var id = {userId}

**Then**
- Get user by id response contains user with id = {userId} and role = USER

### TC-USERS-04 Get all users with non-existing user
**Given**
- No users exist

**When**
- Create user request: page = 0, size = 10

**Then**
- Get users response contains empty list
- Total elements = 0
- Total pages = 0

### TC-USERS-05 Get user by id with non-existing user
**Given**
- No users exist

**When**
- Create user request: path var id = {nonExistingUserId}

**Then**
- Error response: status = 404, message = "User not found with id: {nonExistingUserId}"

### TC-USERS-06 Update user role (happy path)
**Given**
- User exists: id = {userId} role = USER

**When**
- Create user request: path var id = {userId}, request body: role = ADMIN

**Then**
- User role is updated to ADMIN

### TC-USERS-07 Update user role with non-existing role (negative case)
**Given**
- User exists: id = {userId} role = USER

**When**
- Create user request: path var id = {userId}, request body: role = NON_EXISTING_ROLE

**Then**
- Error response: status = 400

### TC-USERS-08 Delete user (happy path)
**Given**
- User exists: id = {userId} role = USER

**When**
- Create user request: path var id = {userId}

**Then**
- Response status = 204
- User is marked deleted

### TC-USERS-09 Delete non-existing user (negative case)
**Given**
- User not exists: id = {userId} role = USER

**When**
- Create user request: path var id = {userId}

**Then**
- Error response status = 404

### TC-USERS-10 Delete deleted user (negative case)
**Given**
- User exists: id = {userId}, role = USER, deleted = true

**When**
- Create user request: path var id = {userId}

**Then**
- Error response status = 404

### TC-USERS-11 Update user role when user non-exist (negative case)
**Given**
- User exists: id = {userId}, role = USER, deleted = true

**When**
- Create user request: path var id = {userId}

**Then**
- Error response status = 404


