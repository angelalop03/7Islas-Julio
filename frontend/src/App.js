import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Register from "./auth/register";
import Login from "./auth/login";
import Logout from "./auth/logout";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import CreateRegisterPlayer from "./auth/register/form/createRegisterPlayer";
import Profile from "./profile/index";
import Rules from "./Reglas/index";
import EditPlayerProfileAndPlayers from "./profile/editProfile/editPlayerProfileAndPlayers";
import EditAdminProfileAndUsers from "./profile/editProfile/editAdminProfileAndUsers";
import EditProfile from "./profile/editProfile/index";
import GameListAdmin from "./admin/games/gameListAdmin";
import Delete from "./auth/delete";
import GameListPlayer from "./player/games/gameListPlayer";
import GameWaitingRoom from "./game/gameWaitingRoom";
import GameCodeRoom from "./game/gameCodeRoom";
import Play from "./player/play/index";
import GameRoom from "./game/gameRoom";
import PlayerListAdmin from "./admin/players/playerListAdmin";
import CreateRegisterAdmin from "./auth/register/form/createRegisterAdmin";
import Reconnect from "./player/reconnect/reconnect";
import EndGameRoom from "./game/endGameRoom";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let userRoutes = <></>;
  let playerRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>

          <Route path="/players" exact={true} element={<PrivateRoute><PlayerListAdmin /></PrivateRoute>} />
          <Route path="/players/:id" exact={true} element={<PrivateRoute><EditPlayerProfileAndPlayers /></PrivateRoute>} />
          <Route path="/users/:id" exact={true} element={<PrivateRoute><EditAdminProfileAndUsers /></PrivateRoute>} />
          <Route path="/gamesAdmin" element={<PrivateRoute><GameListAdmin /></PrivateRoute>} />
          <Route path="/players/new" exact={true} element={<PrivateRoute><CreateRegisterPlayer /></PrivateRoute>} />

          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
        </>)
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/rules" element={<PrivateRoute><Rules /></PrivateRoute>} />
          <Route path="/gamesPlayer" exact={true} element={<PrivateRoute><GameListPlayer /></PrivateRoute>} />
          <Route path="/game/:id/waitingRoom" exact={true} element={<PrivateRoute><GameWaitingRoom /></PrivateRoute>} />
          <Route path="/play" exact={true} element={<PrivateRoute><Play /></PrivateRoute>} />
          <Route path="/gameRoom/:id" exact={true} element={<PrivateRoute><GameRoom /></PrivateRoute>} />
          <Route path="/gameCodeRoom" exact={true} element={<PrivateRoute><GameCodeRoom /></PrivateRoute>} />
          <Route path="/game/:id/waitingRoom" exact={true} element={<PrivateRoute><GameWaitingRoom /></PrivateRoute>} />
          <Route path="/gameRoom/:id" exact={true} element={<PrivateRoute><GameRoom /></PrivateRoute>} />
          <Route path="/backToGame" exact={true} element={<PrivateRoute><Reconnect /></PrivateRoute>} />
          <Route path="/game/:id/endRoom" exact={true} element={<PrivateRoute><EndGameRoom /></PrivateRoute>} />
          </>
      )
    }
  })
  
  if (!jwt) {
    publicRoutes = (
      <>        
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/createRegisterPlayer"element={<CreateRegisterPlayer />} />
        <Route path="/createRegisterAdmin"element={<CreateRegisterAdmin />} />

      </>
    )
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="/createRegisterPlayer"element={<CreateRegisterPlayer />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/profile/edit" element={<EditProfile />} />
        <Route path="/delete" element={<Delete />} />



      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
         
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
