A microservice with Java 21, Spring Boot, MySQL, Spring Data, JUnit 5, and gradle that will address the following business problem:
We want to manage and control our drones with a REST API. Drones move on a field divided in 10x10 sectors. We want to have the following controls per drone:
-Create drone - a drone should have id, name, number of times it moved to a new sector, times when it was created and updated
-Update drone - through the API we should be only able to update the drone’s name
-Delete drone
-List all drones with all their attributes
-Turn left or right - there  are only 4 directions: north, east, south, and west, and we can turn only to a neighbor direction (when facing north can only turn west or east, not south)
   -response should contain drone id, current position (ex: [2, 6]), and facing direction (ex: north)
-Go forward - move 1 sector towards the facing direction
   -response should contain drone id, current position (ex: [2, 6]), and facing direction (ex: north)
   -or it should say that the drone cannot move because it’s at the edge of the field (ex: [9, 9])
-Get current status
   -response should contain drone id, current position (ex: [2, 6]), and facing direction (ex: north)
-For testing, we included Junit Teste, Mockito, integration tests and test containers
