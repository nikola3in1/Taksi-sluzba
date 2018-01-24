-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 24, 2018 at 10:58 PM
-- Server version: 5.7.21-0ubuntu0.16.04.1
-- PHP Version: 7.0.22-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `TaksiSluzba`
--

-- --------------------------------------------------------

--
-- Table structure for table `MENADZER`
--

CREATE TABLE `MENADZER` (
  `MENADZER_ID` char(20) NOT NULL,
  `MENADZER_SIFRA` char(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `MENADZER`
--

INSERT INTO `MENADZER` (`MENADZER_ID`, `MENADZER_SIFRA`) VALUES
('nikola', '7815696ecbf1c96e6894b779456d330e');

-- --------------------------------------------------------

--
-- Table structure for table `ODVEZENA`
--

CREATE TABLE `ODVEZENA` (
  `ID` int(11) NOT NULL,
  `ODVEZENA_DATUM` char(15) NOT NULL,
  `VOZNJA_ID` int(11) NOT NULL,
  `VOZAC_ID` char(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ODVEZENA`
--

INSERT INTO `ODVEZENA` (`ID`, `ODVEZENA_DATUM`, `VOZNJA_ID`, `VOZAC_ID`) VALUES
(30, '2018-01-18', 36, 'Nikola1'),
(31, '2018-01-18', 37, 'Nikola1'),
(34, '2018-01-21', 43, 'Nikola1'),
(35, '2018-01-21', 44, 'Nikola1'),
(36, '2018-01-21', 45, 'moki666'),
(37, '2018-01-21', 46, 'moki666'),
(38, '2018-01-21', 47, 'Nikola1'),
(39, '2018-01-21', 48, 'Nikola1');

-- --------------------------------------------------------

--
-- Table structure for table `SMENA`
--

CREATE TABLE `SMENA` (
  `SMENA_ID` char(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `SMENA`
--

INSERT INTO `SMENA` (`SMENA_ID`) VALUES
('2018-01-18'),
('2018-01-21');

-- --------------------------------------------------------

--
-- Table structure for table `VOZAC`
--

CREATE TABLE `VOZAC` (
  `VOZAC_ID` char(15) NOT NULL,
  `VOZAC_IME` char(20) NOT NULL,
  `VOZAC_PREZIME` char(30) NOT NULL,
  `VOZAC_SIFRA` char(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `VOZAC`
--

INSERT INTO `VOZAC` (`VOZAC_ID`, `VOZAC_IME`, `VOZAC_PREZIME`, `VOZAC_SIFRA`) VALUES
('Mladen123', 'Mladen', 'Ilic', 'b31ed00ee94b1964e97d82a3f6a43378'),
('moki666', 'Marjan', 'Jovanovic', '184fb2a48f3cadfdc33ba33f9ca9e71e'),
('Nikola1', 'Nikola', 'Mitrovic', 'a3dcb4d229de6fde0db5686dee47145d'),
('Uki6A', 'Uros', 'Sibinovic', '6b38a8f02ec9f0c0153c5702275e7168');

-- --------------------------------------------------------

--
-- Table structure for table `VOZNJA`
--

CREATE TABLE `VOZNJA` (
  `VOZNJA_ID` int(11) NOT NULL,
  `VOZNJA_POCETNA_TACKA` char(40) NOT NULL,
  `VOZNJA_KRAJNJA_TACKA` char(40) NOT NULL,
  `VOZNJA_CENA` int(11) NOT NULL,
  `VOZNJA_TRAJANJE` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `VOZNJA`
--

INSERT INTO `VOZNJA` (`VOZNJA_ID`, `VOZNJA_POCETNA_TACKA`, `VOZNJA_KRAJNJA_TACKA`, `VOZNJA_CENA`, `VOZNJA_TRAJANJE`) VALUES
(36, 'Ada Huja (Palilula)', 'Alaska (Zemun)', 200, 0),
(37, 'Ada Huja (Palilula)', 'Zajcova (Voždovac)', 123, 123),
(38, 'Admirala Vukovi?a (Voždovac)', 'Akademska (Zemun)', 206, 65),
(39, 'Ada Huja (Palilula)', 'Adžine Livade (Rakovica)', 214, 145),
(40, 'Ada Huja (Palilula)', 'Adžine Livade (Rakovica)', 200, 0),
(41, 'Abardareva (Palilula)', 'Admirala Vukovi?a (Voždovac)', 213, 135),
(42, 'Admirala Geprata (Savski Venac)', 'Ajzenštajnova (?ukarica)', 200, 5),
(43, 'Ace Joksimovi?a (?ukarica)', 'Ajzenštajnova (?ukarica)', 200, 0),
(44, 'Admirala Geprata (Savski Venac)', 'Akademska (Zemun)', 200, 5),
(45, 'Admirala Geprata (Savski Venac)', 'Akademska (Zemun)', 201, 15),
(46, 'Admirala Geprata (Savski Venac)', 'Akademska (Zemun)', 200, 0),
(47, 'Admirala Geprata (Savski Venac)', 'Akademska (Zemun)', 200, 0),
(48, 'Admirala Geprata (Savski Venac)', 'Akademska (Zemun)', 200, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `MENADZER`
--
ALTER TABLE `MENADZER`
  ADD PRIMARY KEY (`MENADZER_ID`);

--
-- Indexes for table `ODVEZENA`
--
ALTER TABLE `ODVEZENA`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FK_RELATIONSHIP_5` (`VOZAC_ID`),
  ADD KEY `FK_RELATIONSHIP_6` (`VOZNJA_ID`),
  ADD KEY `ODVEZENA_DATUM` (`ODVEZENA_DATUM`);

--
-- Indexes for table `SMENA`
--
ALTER TABLE `SMENA`
  ADD PRIMARY KEY (`SMENA_ID`);

--
-- Indexes for table `VOZAC`
--
ALTER TABLE `VOZAC`
  ADD PRIMARY KEY (`VOZAC_ID`);

--
-- Indexes for table `VOZNJA`
--
ALTER TABLE `VOZNJA`
  ADD PRIMARY KEY (`VOZNJA_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ODVEZENA`
--
ALTER TABLE `ODVEZENA`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;
--
-- AUTO_INCREMENT for table `VOZNJA`
--
ALTER TABLE `VOZNJA`
  MODIFY `VOZNJA_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `ODVEZENA`
--
ALTER TABLE `ODVEZENA`
  ADD CONSTRAINT `FK_RELATIONSHIP_5` FOREIGN KEY (`VOZAC_ID`) REFERENCES `VOZAC` (`VOZAC_ID`),
  ADD CONSTRAINT `FK_RELATIONSHIP_6` FOREIGN KEY (`VOZNJA_ID`) REFERENCES `VOZNJA` (`VOZNJA_ID`),
  ADD CONSTRAINT `ODVEZENA_ibfk_1` FOREIGN KEY (`ODVEZENA_DATUM`) REFERENCES `SMENA` (`SMENA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
