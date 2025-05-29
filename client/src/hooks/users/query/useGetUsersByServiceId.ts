import {useQuery} from '@tanstack/react-query';
import {getAllUsersByServiceId} from '../../../services/user-service';
import Page from '../../../shared/models/api/shared/Page';
import {User} from '../../../shared/models/user.types';

const useGetUsersByServiceId = (page: number, size: number, serviceId: string) => {
  return useQuery<Page<User>>({
    queryKey: ['users', "page", page, "size", size, "serviceId", serviceId],
    queryFn: () => getAllUsersByServiceId(page, size, Number(serviceId)),
  });
};

export default useGetUsersByServiceId;