import Dado0 from '../../static/images/Dado_Start.png';
import Dado1 from '../../static/images/Dado_1.png';
import Dado2 from '../../static/images/Dado_2.png';
import Dado3 from '../../static/images/Dado_3.png';
import Dado4 from '../../static/images/Dado_4.png';
import Dado5 from '../../static/images/Dado_5.png';
import Dado6 from '../../static/images/Dado_6.png';

export default function getImageForResultDice(resultadoTirada) {
    switch (resultadoTirada) {
        case 1:
            return Dado1;
        case 2:
            return Dado2;
        case 3:
            return Dado3;
        case 4:
            return Dado4;
        case 5:
            return Dado5;
        case 6:
            return Dado6;
        default:
            return Dado0;
    }
}