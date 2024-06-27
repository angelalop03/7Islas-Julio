export default function adminChecks(adminDTO) {
    let message;
    
    if (!adminDTO.username) {
        return "Username is required";
    }
    if (!adminDTO.password) {
        return "Password is required";
    }
    if (adminDTO.password.length < 6 || adminDTO.password.length > 20) {
        message = "Password must be between 6 and 20 characters.";
        return message; 
    }

    if (!/\d/.test(adminDTO.password)) {
        message = "The password must contain at least one number.";
        return message; 
    }

    if (!/[A-Z]/.test(adminDTO.password)) {
        message = "The password must contain at least one uppercase letter.";
        return message;
    }

    if (!/[a-z]/.test(adminDTO.password)) {
        message = "The password must contain at least one lowercase letter.";
        return message;
    }
    return null;
}
