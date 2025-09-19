# newsHound

<img width="512" height="512" alt="newsHound" src="https://github.com/user-attachments/assets/16a7fe2f-28e9-4301-b360-3256a6cfbb7c" />

An application to get news articles from the search query based on the intent of the query.
The intent is powered via a LLM model (openAI) and retrived based on the various parameters and segregated into categories.
The app searches the relevant news from the database for all the articles already present and gives the relevant news that matches query and intent.
The output is the list of matching articles along with Short Summary of each of the Article generated via LLM.

To initalize the app
- clone the repository
- install the docker > add mongoDB image on it and execute the same
    ```
    docker run -d --name mongo_db -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=password -p 27017:27017 mongo
    ```
- create a mongoDb database with default username = 'admin',  password = 'password', database = 'demoDB', collection = 'articles'
- execute a docker run with port exposing port: 27017, which will be used to connect the app to the MongoDB database.

To load the articles into database:
- add the news articles to database from the list given
- use this API to upload all articles (if <10)

```
curl --location 'localhost:8080/api/article/saveList' \
--header 'Content-Type: application/json' \
--data-raw '[
    {
    "id": "19aaddc0-7508-4659-9c32-2216107f8604",
    "title": "Attempts to mislead people: B'\''desh leader Yunus on coup rumours",
    "description": "Bangladesh'\''s interim government leader Muhammad Yunus dismissed rumours that a coup is being plotted against him by  the military, calling the claims \"attempts to mislead people\". \"In order to destabi...",
    "url": "https://www.news18.com/amp/world/attempts-to-mislead-people-muhammed-yunus-dismisses-bangladesh-coup-rumours-9274884.html",
    "publication_date": "2025-03-26T04:46:55",
    "source_name": "News18",
    "category": [
      "world"
    ],
    "relevance_score": 0.4,
    "latitude": 17.900636,
    "longitude": 77.465262
  },
  {
    "id": "099503a1-d4b6-460e-ad9c-19212d9dd9ac",
    "title": "Clash erupts in J’khand’s Hazaribagh during Mangla procession",
    "description": "A clash broke out between two groups during Ram Navami Mangla julus (procession) at Jhanda Chowk of Jharkhand'\''s Hazaribagh on Tuesday night, officials said. \"One group was playing some songs during th...",
    "url": "https://www.aninews.in/news/national/general-news/jharkhand-clash-breaks-out-in-hazaribaghs-jhanda-chowk-during-mangla-procession20250326091753/",
    "publication_date": "2025-03-26T04:42:23",
    "source_name": "ANI News",
    "category": [
      "national"
    ],
    "relevance_score": 0.86,
    "latitude": 19.697352,
    "longitude": 73.865399
  }
]'    

```
- if the count is > 10 
  use `localhost:8080/api/article/bulkInsert`. All articles as list in body.


$ To use the app: 
- Add the openAI API key into the environment variables to enable the OPEN AI support and using their models.
    OPENAI_API_KEY : <key>
- Run the Application

API : localhost:8080/api/v1/news/
  Params :
  - user_query : String
  - <custom params> : eg. category, location, lat, long, radius, score, etc.



sample curl
```curl --location --request GET 'localhost:8080/api/v1/news/?user_query=Hazaribagh%20news&category=national' \
--header 'category: national' \
--header 'Content-Type: application/json' \
--data '{
    "title": "Clash erupts in J’khand’s Hazaribagh during Mangla procession",
    "description": "A clash broke out between two groups during Ram Navami Mangla julus (procession) at Jhanda Chowk of Jharkhand'\''s Hazaribagh on Tuesday night, officials said. \"One group was playing some songs during th...",
    "url": "https://www.aninews.in/news/national/general-news/jharkhand-clash-breaks-out-in-hazaribaghs-jhanda-chowk-during-mangla-procession20250326091753/",
    "publicationDate": "2025-03-26T04:42:23",
    "sourceName": "ANI News",
    "category": [
        "national"
    ]
}'
```
