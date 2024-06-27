export default function PlayerRegisterForm(playerDTO) {
    let message;

    if (/^\d+$/.test(playerDTO.username)) {
        message = "The username cannot contain only numbers.";
        return message;
    }

    if (playerDTO.password.length < 6 || playerDTO.password.length > 20) {
        message = "Password must be between 6 and 20 characters.";
        return message; 
    }

    if (!/\d/.test(playerDTO.password)) {
        message = "The password must contain at least one number.";
        return message; 
    }

    if (!/[A-Z]/.test(playerDTO.password)) {
        message = "The password must contain at least one uppercase letter.";
        return message;
    }

    if (!/[a-z]/.test(playerDTO.password)) {
        message = "The password must contain at least one lowercase letter.";
        return message;
    }

    if (/\d/.test(playerDTO.firstName)) {
        message = "The first name cannot contain numbers.";
        return message; 
    } 
    
    if (/\d/.test(playerDTO.lastName)) {
        message = "The last name cannot contain numbers.";
        return message; 
    } 
    
    const currentDate = new Date();
    const minAgeDate = new Date();
    minAgeDate.setFullYear(currentDate.getFullYear() - 10);
    if (new Date(playerDTO.birthdayDate) > minAgeDate) {
        message = "You must be at least 10 years old to register.";
        return message;
    }

    const emailRegex = /^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(playerDTO.email)) {
        message = "Please enter a valid email address.";
        return message;
    }

    return null; // No errors
}
