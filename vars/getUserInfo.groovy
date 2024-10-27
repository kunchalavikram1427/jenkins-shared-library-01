import groovy.json.JsonSlurper

/**
 * This function retrieves user information based on the provided username.
 * @param username - The username of the user to retrieve info for.
 * @return Map containing user information as JSON data, or null if user not found.
 */

def call(String username) {
    // Load and parse the JSON file
    def usersJson = libraryResource('org/dme/users.json')
    def users = new JsonSlurper().parseText(usersJson)

    // Find the user by username
    def userInfo = users.find { it.username == username }
    
    // Return user info as JSON or null if not found
    return userInfo ?: "User not found"
}


def getMultiUserInfo(List<String> usernames) {
    // Load and parse the JSON file
    def usersJson = libraryResource('users.json')
    def users = new JsonSlurper().parseText(usersJson)

    // Find users by matching usernames
    def userInfoList = users.findAll { it.username in usernames }
    
    // Return list of user info JSON or empty list if none found
    return userInfoList
}