-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Oct 15, 2022 at 03:00 PM
-- Server version: 8.0.27
-- PHP Version: 7.4.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `delorest`
--
CREATE DATABASE IF NOT EXISTS `delorest` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `delorest`;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
CREATE TABLE IF NOT EXISTS `reports` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(50) NOT NUll,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reports`
--

INSERT INTO `reports` (`id`, `name`, `type`, `created_at`, `updated_at`) VALUES
(1, 'foodsMenu.jrxml', 'foodsMenu', '2022-10-27 11:44:59', '2022-10-27 11:44:59'),
(2, 'juicesMenu.jrxml', 'juicesMenu', '2022-10-27 11:44:59', '2022-10-27 11:44:59'),
(3, 'orders.jrxml', 'orders', '2022-10-27 11:44:59', '2022-10-27 11:44:59'),
(4, 'orderID.jrxml', 'orderID', '2022-10-27 11:44:59', '2022-10-27 11:44:59');

--
-- Table structure for table `cates`
--

DROP TABLE IF EXISTS `cates`;
CREATE TABLE IF NOT EXISTS `cates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL,
  `description` mediumtext,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
--
-- Table structure for table `foods`
--

DROP TABLE IF EXISTS `foods`;
CREATE TABLE IF NOT EXISTS `foods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image` varchar(80) NOT NULL DEFAULT 'd3e30b5f4.jpg',
  `name` varchar(50) NOT NULL,
  `cate_id` INT NOT NULL,
  `price` int NOT NULL,
  `description` mediumtext,
  `created_at` varchar(50) NOT NULL,
  `updated_at` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `juices`
--

DROP TABLE IF EXISTS `juices`;
CREATE TABLE IF NOT EXISTS `juices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image` varchar(80) NOT NULL DEFAULT '45hg64jf4.jpg',
  `name` varchar(50) NOT NULL,
  `type` varchar(10) NOT NULL,
  `price` int NOT NULL,
  `description` mediumtext,
  `created_at` varchar(50) NOT NULL,
  `updated_at` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` varchar(80) NOT NULL,
  `user_id` int NOT NULL,
  `order` varchar(200) NOT NULL,
  `price` varchar(200) NOT NULL,
  `full_price` int NOT NULL,
  `created_at` varchar(50) NOT NULL,
  `updated_at` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `profile` varchar(80) NOT NULL DEFAULT 'ca.png',
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `job` varchar(11) NOT NULL,
  `password` varchar(100) NOT NULL,
  `b_email` varchar(100) DEFAULT NULL DEFAULT 'Not Set',
  `phone` varchar(15) DEFAULT NULL DEFAULT 'Not Set',
  `created_at` varchar(50) NOT NULL,
  `updated_at` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `profile`, `username`, `email`, `job`, `password`, `b_email`, `phone`, `created_at`, `updated_at`) VALUES
(1, 'su.png', 'Super User', 'su@gmail.com', 'Admin', '0f1ba603c1a843a3d02d6c5038d8e959', 'Not Set', 'Not Set', 'OCTOBER 27, 2022', 'OCTOBER 27, 2022'),
(2, 'ca.png', 'Cashier', 'ca@gmail.com', 'Cashier', '0f1ba603c1a843a3d02d6c5038d8e959', 'Not Set', 'Not Set', 'OCTOBER 27, 2022', 'OCTOBER 27, 2022');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
