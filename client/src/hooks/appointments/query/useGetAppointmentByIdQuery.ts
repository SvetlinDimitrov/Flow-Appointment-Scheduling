import {useQuery} from '@tanstack/react-query';
import {getAppointmentById} from "../../../services/appintment-service.ts";

const useGetAppointmentByIdQuery = (id: number | null) => {
  return useQuery({
    queryKey: ['appointment', id],
    queryFn: () => {
      if (id !== null) {
        return getAppointmentById(id);
      }
      return Promise.reject(new Error('Invalid appointment ID'));
    },
    enabled: id !== null,
  });
};

export default useGetAppointmentByIdQuery;