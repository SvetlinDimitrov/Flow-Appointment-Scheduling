import {CircularProgress} from "@mui/material";
import styles from './LoadingSpinner.module.css';

const LoadingSpinner = () => {

  return (
      <div className={styles.spinnerContainer}>
        <CircularProgress color={'primary'} size={80}/>
      </div>
    );
};

export default LoadingSpinner;