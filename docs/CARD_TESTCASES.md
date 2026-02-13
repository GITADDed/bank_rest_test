# Test Cases

## Cards

### TC-CARDS-01 Create card (happy path)
**Given**
- User exists: id = {ownerId}, role = USER

**When**
- Create card request: ownerId = {ownerId}, pan = 1234567890123456, expiryMonth = 10, expiryYear = 2028 (valid value)

**Then**
- Card is created
- last4 is computed
- status is ACTIVE
- balance is 0

### TC-CARDS-02 Create card with invalid PAN
**Given**
- User exists: id = {ownerId}, role = USER

**When**
- Create card request with pan = "123"

**Then**
- Validation error is returned
- No card is created

### TC-CARDS-03 Create card with expired date
**Given**
- User exists: id = {ownerId}, role = USER

**When**
- Create card request with expiryMonth and expiryYear in the past
- example expiryMonth = 1, expiryYear = 2020

**Then**
- Validation error is returned
- No card is created

### TC-CARDS-04 Create card with non-existing user
**Given**
- No user with id = {ownerId} exists

**When**
- Create card request: ownerId = {ownerId}, pan = 1234567890123456, expiryMonth = 10, expiryYear = 2028

**Then**
- Error is returned indicating user not found
- No card is created


