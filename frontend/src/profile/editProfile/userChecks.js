export default function userChecks(user, user1) {
    let message;
    if (/^\d+$/.test(user.username)) {
        message=("The username cannot contain only numbers.");
        return message;
    }
    

    if (user.password !== user1.password && (user.password.length < 6 || user.password.length > 20)) {
        message="Password must be between 6 and 20 characters.";
        return message; 
    }

    if (user.password !== user1.password && !/[A-Z]/.test(user.password)) {
        message= "The password must contain at least one uppercase letter.";
        return message; 
    }
    if (user.password !== user1.password && !/[a-z]/.test(user.password)) {
        message= "The password must contain at least one lowercase letter.";
        return message; 
    }

    if (user.password !== user1.password && !/\d/.test(user.password)) {
        message= "The password must contain at least one number.";
        return message; 
    }

  
    return null; // No errors


}