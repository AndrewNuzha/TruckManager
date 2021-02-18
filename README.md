# Truck Manager Game
### Description:
This is a simple transport company simulation game.\
During the game, you can send shipments with trucks, service them, sell and buy new ones.
### Gameplay instructions:
#### 1. Registration:
First of all, you have to create an account. Go to **/registration** page and fill all the fields.\
The program will check if there is already such a nickname and company name.\
If everything is ok, you will be redirected to the main page of your company **/homepage**\
If you already have an account, follow **/login** page and enter your nickname and password.
#### 2. Truck management:
On the page **/trucks** you see a list of your company's trucks and their information.\
You can view the details of the truck on modal window (press 'details' button) and also sell it or service it.
#### 3. Shipment management:
If you press 'New shipment' button, you will see a list of available routes for the truck.\
Each route has a different distance and travel time.\
If the list of routes is empty, then the truck needs service.

Press 'Take this order' button to send the truck on the route.\
After you can see this shipment on the page **/shipments**.
You can check the remaining time and progress in real time.\
When the truck is on the route, you cannot select another shipment for this truck.\
You have to wait for the truck to arrive at its destination point. Then he will be available again for a new shipment.

#### 4. Buying new trucks:
On the page **/truckstore** you can select on of the new trucks and buy it. 

### Setup
#### 1. Create new docker container:
Open your terminal and enter:\
**docker run -e MYSQL_ROOT_PASSWORD=123 -d -p 127.0.0.1:3307:3306 mysql:8**
#### 2. Initialize database:
Execute script: 
https://github.com/AndrewNuzha/TruckManager/blob/master/src/main/resources/InitializeDB.sql
