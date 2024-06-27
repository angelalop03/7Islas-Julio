import "../static/css/player/Profile.css";
import tokenService from "../services/token.service";
import AdminProfileComponent from "./adminProfileComponent";
import PlayerProfileComponent from "./playerProfileComponent";



export default function EditProfile() {
  const handleEditProfile = () => {
    window.location.href = '/profile/edit';
};

const handleDeleteAccount = () => {
    window.location.href = '/delete';
};

return (
    <div className="container-page">
        <div className="player-profile-container">
            
            <div className="player-profile-container2">
                {tokenService.getUser().roles[0] === 'ADMIN' && <AdminProfileComponent />}
                {tokenService.getUser().roles[0] === 'PLAYER' && <PlayerProfileComponent />}
            </div>

            <div className="ButtonPartContainer">
                <button onClick={handleEditProfile} className="button">
                    Edit profile
                </button>
                <button onClick={handleDeleteAccount} className="buttonDelete">
                    Delete Account
                </button>
            </div>

        </div>
    </div>
);
}
