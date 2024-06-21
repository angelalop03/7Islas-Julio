import Estandar from '../static/images/estandar.jpg';
import foto1 from '../static/images/FotoPerfil1.jpg';
import foto2 from '../static/images/FotoPerfil2.jpg';
import foto3 from '../static/images/FotoPerfil3.jpg';
import foto4 from '../static/images/FotoPerfil4.jpg';



export default function getProfileImage(props) {
    switch (props) {
        case 'Estandar':
            return Estandar;
        case 'foto1':
            return foto1;
        case 'foto2':
            return foto2;
        case 'foto3':
            return foto3;
        case 'foto4':
            return foto4;
        default:
            return Estandar;
    }
}