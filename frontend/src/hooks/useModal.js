import { useState } from 'react';

export const useModal = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [data, setData] = useState(null);

    const open = (initialData = null) => {
        setData(initialData);
        setIsOpen(true);
    };

    const close = () => {
        setData(null);
        setIsOpen(false);
    };

    return {
        isOpen,
        data,
        open,
        close
    };
};
