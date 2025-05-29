import {useState} from 'react';
import {UseQueryResult} from '@tanstack/react-query';
import Page from '../../shared/models/api/shared/Page';

const usePaginatedQuery = <T>(
  queryHook: (page: number, size: number, ...args: string[]) => UseQueryResult<Page<T>>,
  initialPage: number = 0,
  size: number = 10,
  ...args: string[]
) => {
  const [page, setPage] = useState(initialPage);

  const {data, isLoading, error , isFetching} = queryHook(page, size, ...args);

  if (data && data.content.length === 0 && page !== 0 && !isFetching) {
    setPage(page - 1);
  }

  const handleNextPage = () => {
    if (data && page < data.totalPages - 1) {
      setPage((prev) => prev + 1);
    }
  };

  const handlePreviousPage = () => {
    if (page > 0) {
      setPage((prev) => prev - 1);
    }
  };

  const handlePageChange = (value: number) => {
    if (value > page + 1) {
      handleNextPage();
    } else {
      handlePreviousPage();
    }
  };

  return {
    data,
    isLoading,
    error,
    page,
    handlePageChange,
    setPage
  };
};

export default usePaginatedQuery;