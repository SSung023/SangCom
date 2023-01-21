import { combineReducers } from 'redux';
import loginReducer from './loginReducer';
import jwtReducer from './jwtReducer';

const rootReducer = combineReducers({
    loginReducer,
    jwtReducer
});

export default rootReducer;