# Path to Modernisation

## Kitchen Sink Requirements

### API

- API POST Register Member
- API GET Member List
- API GET Member by ID

### UI

- Ability to register name, email and phone number
    - Field level validations for name, email and phone
    - Unique constraint on email

## Technical Stack

JBoss EAP 8.0

JSF, CDI, JAX-RS, EJB, JPA, and Bean Validation

## Process of Migration

1. Introduce Quarkus Dependencies and update to use Java 17
2. Update folder structure to separate Legacy and Modern java classes
3. Start by updating one API resource at a time migrating over
4. Once API layer is done we can start to look at migrating UI elements as it is using underlying service
5. Update java dependency to 21
6. Migrate to MongoDB