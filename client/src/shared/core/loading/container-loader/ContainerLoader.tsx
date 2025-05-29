import { Box, CircularProgress } from "@mui/material";

interface LoaderProps {
  height: string | number;
}

/**
 * ContainerLoader component displays a loading spinner and an overlay to block user interactions.
 *
 * This component consists of two `Box` elements:
 * 1. The first `Box` centers a `CircularProgress` spinner within the specified height.
 * 2. The second `Box` covers the entire screen with a semi-transparent overlay to prevent user interactions.
 *
 */
const ContainerLoader = ({ height }: LoaderProps) => {
  return (
    <>
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        width="100%"
        height={height}
        zIndex={1000}
      >
        <CircularProgress />
      </Box>
      <Box
        position="fixed"
        top={0}
        left={0}
        width="100%"
        height="100%"
        zIndex={999}
      />
    </>
  );
};

export default ContainerLoader;