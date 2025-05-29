import { useState } from 'react';

const useProcessing = () => {
  const [processing, setProcessing] = useState(false);

  const startProcessing = () => setProcessing(true);
  const stopProcessing = () => setProcessing(false);

  return {
    processing,
    startProcessing,
    stopProcessing,
  };
};

export default useProcessing;