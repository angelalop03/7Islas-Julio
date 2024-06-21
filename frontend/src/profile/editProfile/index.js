import tokenService from "../../services/token.service";
import EditAdminProfileAndUsers from "./editAdminProfileAndUsers";
import EditPlayerProfileAndPlayers from "./editPlayerProfileAndPlayers";

export default function EditProfile() {
  return (
    <>
      {tokenService.getUser() && tokenService.getUser().roles[0] === "ADMIN" && <EditAdminProfileAndUsers />}
      {tokenService.getUser() && tokenService.getUser().roles[0] === "PLAYER" && <EditPlayerProfileAndPlayers />}
    </>
  );
}