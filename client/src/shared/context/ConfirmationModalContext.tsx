import {createContext, ReactNode, useContext, useState} from 'react';

interface ConfirmationModalContextProps {
  openModal: (title: string, message: string, onConfirm: () => void) => void;
  closeModal: () => void;
  modalData: {
    isOpen: boolean;
    title: string;
    message: string;
    onConfirm: () => void;
  };
}

const ConfirmationModalContext = createContext<ConfirmationModalContextProps | undefined>(undefined);

export const ConfirmationModalProvider = ({children}: { children: ReactNode }) => {
  const [modalData, setModalData] = useState({
    isOpen: false,
    title: '',
    message: '',
    onConfirm: () => {
    },
  });

  const openModal = (title: string, message: string, onConfirm: () => void) => {
    setModalData({
      isOpen: true,
      title,
      message,
      onConfirm,
    });
  };

  const closeModal = () => {
    setModalData((prevState) => ({
      ...prevState,
      isOpen: false,
    }));
  };

  return (
    <ConfirmationModalContext.Provider value={{openModal, closeModal, modalData}}>
      {children}
    </ConfirmationModalContext.Provider>
  );
};

export const useConfirmationModal = () => {
  const context = useContext(ConfirmationModalContext);
  if (!context) {
    throw new Error('useConfirmationModal must be used within a ConfirmationModalProvider');
  }
  return context;
};