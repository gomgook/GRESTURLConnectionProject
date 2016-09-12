# Nproject

Nproject is the test project to test a connection module which use REST API. Build.gradle which is dependent upon GRESTURLConnection module makes .lib file automatically and dependency of the file on app module to use this .lib file.

# GRESTURLConnection

GRESTURLConnection is the REST API connection module library. It provides HTTP & HTTPS connection. If you should use this library, import the .lib file which is the output of the build.gradle file.

GRESTURLConnection provides the functions below:

1. HTTP & HTTPS connection
2. Inputting REST API request types
3. Setting connection parameters
4. Setting connection timeout
5. Setting connection headers
6. Setting connection request bodies
7. Getting connection result as String type to get all types of results
