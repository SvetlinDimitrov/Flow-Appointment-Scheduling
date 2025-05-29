import {useInfiniteQuery} from '@tanstack/react-query';
import {getAllUsers} from '../../../services/user-service';
import Page from '../../../shared/models/api/shared/Page';
import {User, UserRole} from '../../../shared/models/user.types';

const useInfiniteUsersByRole = (page: number, size: number, userRole: UserRole, sort?: string, search?: string) => {
  return useInfiniteQuery<Page<User>>({
    queryKey: ['users', 'userRole', userRole, 'page', page, 'size', size, 'sort', sort, 'search', search],
    queryFn: ({pageParam = page}) => getAllUsers(pageParam as number, size, userRole, sort, search),
    getNextPageParam: (lastPage) => {
      if (lastPage.number < lastPage.totalPages - 1) {
        return lastPage.number + 1;
      }
      return undefined;
    },
    initialPageParam: page,
  });
};

export default useInfiniteUsersByRole;