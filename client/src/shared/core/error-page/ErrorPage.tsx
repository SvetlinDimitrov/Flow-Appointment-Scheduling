import styled, { createGlobalStyle } from 'styled-components';
import { Button, Typography } from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFrownOpen } from '@fortawesome/free-regular-svg-icons';
import { useNavigate } from 'react-router-dom';

const GlobalStyle = createGlobalStyle`
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        font-family: "Consolas", monospace;
    }
`;

const Header = styled.header`
  width: 100%;
  height: 86.1vh;
  background: #1b1818;
  display: flex;
  align-items: center;
  justify-content: center;

  @media only screen and (prefers-color-scheme: light) {
    background: #f3eaea;
  }
`;

const Container = styled.div`
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  flex-direction: row-reverse;

  @media only screen and (max-width: 800px) {
    flex-direction: column;
  }
`;

const IconContainer = styled.div`
  color: #18d1d1;
  font-size: 200px;

  @media only screen and (prefers-color-scheme: light) {
    color: #0099ff;
  }
`;

const TextContainer = styled.div`
  color: #fff;
  text-align: center;

  h2 {
    font-size: 60px;

    @media only screen and (max-width: 800px) {
      font-size: 50px;
    }
  }

  p {
    max-width: 500px;
  }

  span {
    color: #18d1d1;

    @media only screen and (prefers-color-scheme: light) {
      color: #0099ff;
    }
  }

  @media only screen and (prefers-color-scheme: light) {
    color: #111;
  }
`;

const BtnContainer = styled.div`
  margin-top: 20px;
`;

const StyledButton = styled(Button)`
  color: #1b1818 !important;
  padding: 10px !important;
  background: #18d1d1 !important;
  border-radius: 10px !important;
  font-weight: 530 !important;
  font-size: 15px !important;
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }

  &:active {
    opacity: 0.5;
  }

  @media only screen and (prefers-color-scheme: light) {
    color: #f3eaea !important;
    background: #0099ff !important;
  }
`;

const ErrorPage = () => {
  const navigate = useNavigate();

  return (
    <>
      <GlobalStyle />
      <Header>
        <Container>
          <IconContainer>
            <FontAwesomeIcon icon={faFrownOpen} />
          </IconContainer>
          <TextContainer>
            <Typography variant="h2">
              Random Error <span>ABC</span>!
            </Typography>
            <Typography variant="body1">
              The page you are trying to connect is <span>not available</span> or some <span>error</span> occurred! Our team is working on fixing it.
            </Typography>
            <BtnContainer>
              <StyledButton onClick={() => navigate('/')}>
                Go Home
              </StyledButton>
            </BtnContainer>
          </TextContainer>
        </Container>
      </Header>
    </>
  );
};

export default ErrorPage;