import { useNavigate } from 'react-router-dom';
import styled, { createGlobalStyle } from 'styled-components';

const GlobalStyle = createGlobalStyle`
  body {
    margin: 0px;
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background-repeat: no-repeat;
    background-size: 100%;
    background: radial-gradient(white, #CCCDD1);
  }
`;

const Container = styled.div`
  display: flex;
  height: 100%;
  width: 100%;
  align-items: center;
  justify-content: center;
`;

const SignWrapper = styled.div`
  width: 400px;
  height: 100%;
`;

const Sign = styled.div`
    margin: 150px;
    background: radial-gradient(yellow, #EDC612);
    height: 150px;
    width: 150px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    border: 4px solid #EDC612;
    box-shadow: inset 0px 0px 0px 4px #1d1d1d;
    transform: rotate(45deg);
    position: relative;
    z-index: 1;

    &::before {
        position: absolute;
        z-index: -1;
        content: "";
        background: linear-gradient(to right, #8C8C8C, #AFAFAF 20%, #8C8C8C 80%);
        height: 200px;
        width: 10px;
        transform-origin: top;
        transform: rotate(-45deg);
        top: 150px;
        left: 145px;
    }

    .message {
        transform: rotate(-45deg);
        font-family: 'Montserrat', sans-serif;
        color: #1d1d1d;
        font-weight: bold;
        font-size: 70px;

        &::before, &::after {
            position: absolute;
            content: "";
            height: 6px;
            width: 6px;
            background: linear-gradient(to right, #ccc, #AFAFAF);
            left: 66px;
            border-radius: 50%;
            top: -50px;
        }

        &::after {
            top: 130px;
        }
    }

    .redirect {
        position: absolute;
        top: 170px;
        left: 80px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        width: 150px;
        background: radial-gradient(#25DA6E, #4BB45B);
        transform-origin: top;
        transform: rotate(-48deg);
        transform-style: preserve-3d;
        backface-visibility: hidden;
        font-family: 'Montserrat', sans-serif;
        padding: 10px;
        box-shadow: inset 0px 0px 0px 3px white;
        border-radius: 5px;
        border: 5px solid #4BB45B;

        a {
            color: white;
            text-decoration: none;
            text-transform: uppercase;
        }

        &::before {
            position: absolute;
            content: "";
            height: 6px;
            width: 6px;
            background: linear-gradient(to right, #ccc, #AFAFAF);
            border-radius: 50%;
            left: 81px;
        }
    }
`;

const Info = styled.div`
  width: 350px;

  h1 {
    color: #EDC612;
    font-family: 'Montserrat', sans-serif;
    font-weight: bold;
  }

  p {
    font-family: 'Montserrat', sans-serif;
    color: #1d1d1d;
    font-size: 16px;
  }
`;

const PageNotFound = () => {
  const navigate = useNavigate();

  return (
    <>
      <GlobalStyle />
      <Container>
        <SignWrapper>
          <Sign>
            <div className="message">404</div>
            <div className="redirect">
              <a href="#" onClick={() => navigate('/')}>back to home</a>
            </div>
          </Sign>
        </SignWrapper>
        <Info>
          <h1>Oops, you've come to a "dead end"</h1>
          <p>The page you were looking for was probably moved, deleted, eaten, went on vacation or never existed.</p>
        </Info>
      </Container>
    </>
  );
};

export default PageNotFound;