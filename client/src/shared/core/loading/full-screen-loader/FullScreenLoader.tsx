import {Box, CircularProgress} from '@mui/material';

const FullScreenLoader = ({isLoading}: { isLoading: boolean }) => {
  if (!isLoading) return null;

  return (
    <Box
      position={'fixed'}
      top={0}
      left={0}
      width={'100%'}
      height={'100%'}
      bgcolor={'rgba(0, 0, 0, 0.5)'}
      display={'flex'}
      alignItems={'center'}
      justifyContent={'center'}
      zIndex={999999}>
      <CircularProgress color={'primary'} size={80}/>
    </Box>
  );
};

export default FullScreenLoader;