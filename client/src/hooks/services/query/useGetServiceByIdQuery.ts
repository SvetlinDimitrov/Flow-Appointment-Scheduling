import { useQuery } from '@tanstack/react-query';
import { getServiceById } from '../../../services/service-service.ts';
import { Service } from '../../../shared/models/service.types.ts';

const useGetServiceByIdQuery = (id: string | undefined) => {
  const queryKey = ['service', id];
  const numericId = Number(id);

  return useQuery<Service>({
    queryKey,
    queryFn: () => {
      if (isNaN(numericId)) {
        return Promise.reject(new Error("Invalid ID"));
      }
      return getServiceById(numericId);
    },
    enabled: !!id,
  });
};

export default useGetServiceByIdQuery;